/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class ServerMultiThreaded  {
  
 	/**
  	* main method
	* @param EchoServer port
  	* 
  	**/
   public static void main(String args[]){ 
    ServerSocket listenSocket;
    
  	if (args.length != 1) {
          System.out.println("Usage: java EchoServer <EchoServer port>");
          System.exit(1);
  	}
	try {
	      File historic = new File("./Code-Socket/files/historic.txt");
	      if (historic .createNewFile()) {
	        System.out.println("File created: " + historic .getName());
	      } else {
	        System.out.println("Historique existant.");
	      }
		listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
		System.out.println("Server ready..."); 
		while (true) {
			Socket clientSocket = listenSocket.accept();
			System.out.println("Connexion from:" + clientSocket.getInetAddress());
			ClientThread ct = new ClientThread(clientSocket);
			ct.start();
		}
    }  catch (IOException e) {
	      System.out.println("An error occurred.");
  	      e.printStackTrace();
  	} catch (Exception e) {
        System.err.println("Error in EchoServer:" + e);
    }
  }
}

  