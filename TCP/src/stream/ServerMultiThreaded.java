/***
 * EchoServer
 * @author Pierre-Louis Jallerat et Mickael Bensaid
 */

package stream;

import java.io.*;
import java.net.*;

/**
 * 
 * Cette classe ecoute les demandes de connexions des clients
 * Pour chaque client, la classe ClientThread est intancie dans un nouveau thread et
 * gere l'emission et la reception de message
 *
 */

public class ServerMultiThreaded  {
  
	/**
	 * @param args port du serveur
	 */
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

  