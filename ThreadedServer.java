package ee402;


import java.net.*;
import java.io.*;




// threaded server class built on DCU example code
public class ThreadedServer 
{
	private static int portNumber = 5050;
	
	public static void main(String args[]) {
		
		boolean listening = true;
        ServerSocket serverSocket = null;
        ServerGUI ServerGUI = new ServerGUI();
        //The id service is used to manage our clients, giving a unique ID to each one of them
        IdService id = new IdService();
        
        // Set up the Server Socket
        try 
        {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("New Server has started listening on port: " + portNumber );
        } 
        catch (IOException e) 
        {
            System.out.println("Cannot listen on port: " + portNumber + ", Exception: " + e);
            System.exit(1);
        }
        
        // Server is now listening for connections
        while (listening) // almost infinite loop - loop once for each client request
        {
            Socket clientSocket = null;
            try{
            	System.out.println("**. Listening for a connection...");
                clientSocket = serverSocket.accept();
                System.out.println("01. <- Accepted socket connection from a client: ");
                System.out.println("    <- with address: " + clientSocket.getInetAddress().toString());
                System.out.println("    <- and port number: " + clientSocket.getPort());
            } 
            catch (IOException e){
                System.out.println("XX. Accept failed: " + portNumber + e);
                listening = false;   // end the loop - stop listening for further client requests
            }	
            
            //Conditional statement needed to make sure that there will not be more than 5 clients connected at the same time 
            if(!id.isEmpty()){
	            ThreadedConnectionHandler con = new ThreadedConnectionHandler(clientSocket, ServerGUI, id);
	            con.start(); 
            } else System.out.println("The number of connected clients is too high, please note tha a maximum of 5 client per session can be handled.");
            System.out.println("02. -- Finished communicating with client:" + clientSocket.getInetAddress().toString());
        }
        
        // Server is no longer listening for client connections - time to shut down.
        try 
        {
            System.out.println("04. -- Closing down the server socket gracefully.");
            serverSocket.close();
        } 
        catch (IOException e) 
        {
            System.err.println("XX. Could not close server socket. " + e.getMessage());
        }
    }
}