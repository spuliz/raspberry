package ee402;
import java.net.*;
import java.io.*;

public class Client extends Thread{
	
	private static int portNumber = 5050;
    private Socket socket = null;
    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;

	// the constructor expects the IP address of the server - the port is fixed
    public Client(String serverIP) {
    	if (!connectToServer(serverIP)) {
    		System.out.println("XX. Failed to open socket connection to: " + serverIP);            
    	}
    }

    private boolean connectToServer(String serverIP) {
    	try { // open a new socket to the server 
    		this.socket = new Socket(serverIP,portNumber);
    		this.os = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is = new ObjectInputStream(this.socket.getInputStream());
    		System.out.println("00. -> Connected to Server:" + this.socket.getInetAddress() 
    				+ " on port: " + this.socket.getPort());
    		System.out.println("    -> from local address: " + this.socket.getLocalAddress() 
    				+ " and port: " + this.socket.getLocalPort());
    	} 
        catch (Exception e) {
        	System.out.println("XX. Failed to Connect to the Server at port: " + portNumber);
        	System.out.println("    Exception: " + e.toString());	
        	return false;
        }
		return true;
    }

	
    // method to send a generic object.
    private void send(Object o) {
		try {
		    os.writeObject(o);
		    os.flush();
		    System.out.println("Parcel succesfully sent");
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Sending:" +  e.toString());
		}
    }

    // method to receive a generic object.
    private Object receive() 
    {
		String o = null;
		try {
		    o = (String) is.readObject();
		} 
	    catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Receiving:" + e.toString());
		}
		return o;
    }

    public static void main(String args[]) 
    {    	
    	int samplingTime= 0, ParcelNumber = 1;
    	System.out.println("**. Java Client Application - EE402 OOP Module, DCU");
    	
    	if(args.length==1 || args.length==2){
    		Client theApp = new Client(args[0]);
    		if(args.length==1){
    			samplingTime= 3000;
    		}else samplingTime=  Integer.parseInt(args[1]);
    		
    		System.out.println("Sampling Time is set to " + samplingTime);
    		System.out.println("");
    		
    		while(true){
    			Parcel Parcel = new Parcel();
    			theApp.send(Parcel);
    			System.out.println("Temperature : " + Parcel.getTemperature());
    			System.out.println("Time and Date of Parcel : " + Parcel.getTimeDate());
    			System.out.println("Parcels sent since start : " + ParcelNumber);
    			ParcelNumber ++;
    			
    			System.out.println((String) theApp.receive());
    			System.out.println("");    			
    			
	    		try {
					Thread.sleep(samplingTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
		}
    	else
    	{
    		System.out.println("Error: you must provide the address of the server");
    		System.out.println("Usage is:  java Client x.x.x.x  [samplingTime] (e.g. java Client 192.168.7.2 3000)");
    		System.out.println("      or:  java Client hostname [samplingTime] (e.g. java Client localhost 3000)");
    	}    
    	System.out.println("**. End of Application.");
    	
    }
}