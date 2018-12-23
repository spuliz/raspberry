package ee402;


import java.net.*;
import java.io.*;

public class ThreadedConnectionHandler extends Thread
{
    private Socket clientSocket = null;				// Client socket object
    private ObjectInputStream is = null;			// Input stream
    private ObjectOutputStream os = null;			// Output stream
    private ServerGUI ServerGUI = null;
    private IdService idService = null;
    private int id = 0;
    
	// The constructor for the connection handler
    public ThreadedConnectionHandler(Socket clientSocket, ServerGUI ServerGUI, IdService id) {
        this.clientSocket = clientSocket;
        this.ServerGUI = ServerGUI;
        this.idService = id;
        this.id = id.getAvailable();
    }

    // Will eventually be the thread execution method - can't pass the exception back
    public void run() {
         try {
            this.is = new ObjectInputStream(clientSocket.getInputStream());
            this.os = new ObjectOutputStream(clientSocket.getOutputStream());
            //Calling back the server UI, we make sure the id is cleared from the previous client 
            this.ServerGUI.clearUser(this.id);
            while (this.readData()) {}
            //If one of the five client loose the connection, we make sure that its assigned id is available
            this.idService.makeAvailable(this.id);
         } 
         catch (IOException e) 
         {
        	System.out.println("XX. There was a problem with the Input/Output Communication:");
            e.printStackTrace();
         }
         
         System.out.println("The client with id " + this.id + " is disconnected");
    }

    // Receive and process incoming Parcel from client socket 
    private boolean readData() {
        Parcel s = null;
        try {
            s = (Parcel) is.readObject();
        } 
        catch (Exception e){    // catch a general exception
        	this.closeSocket();
            return false;
        }
        System.out.println("01. <- Received a Parcel at " + s.getTimeDate() + ".");
        
        this.send(new String("Parcel succesfully received"));
        
        this.ServerGUI.updateLists(s, this.id);

        return true;
    }


    // Send a generic object back to the client 
    private void send(Object o) {
        try {
            System.out.println("02. -> Sending (" + o +") to the client.");
            this.os.writeObject(o);
            this.os.flush();
        } 
        catch (Exception e) {
            System.out.println("XX." + e.getStackTrace());
        }
    }
    
    // Send a pre-formatted error message to the client 
    public void sendError(String message) { 
        this.send("Error:" + message);	//remember a String IS-A Object!
    }
    
    // Close the client socket 
    public void closeSocket() { //gracefully close the socket connection
        try {
            this.os.close();
            this.is.close();
            this.clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println("XX. " + e.getStackTrace());
        }
    }
}