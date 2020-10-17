package stream;

import java.io.*;
import java.net.Socket;

public class ServerListenerThread extends Thread {
		
	private Socket clientSocket;
	private BufferedReader socIn;
	
	ServerListenerThread(Socket s) {
		this.clientSocket = s;
	}
	
	public void closeConnection() { 
	    try {
	        socIn.close();
	    } catch (IOException ex) {
	        System.out.println("Error closing the socket and streams");
	    }
	 
	}

 	/**
  	* receives a request from the clientThread then read the message
  	* @param clientSocket
  	**/
	public void run() {
    	  try {
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream())); 
    		while (true) {
    		  String line = socIn.readLine();
    		  System.out.println(line);
    		}
    	} catch (Exception e) {
        	System.err.println("Error in clientSocket:" + e); 
        }
       }

}