/**
 * ServerListenerThread
 * @author Pierre-Louis Jallerat et Mickael Bensaid
 */
package stream;

import java.io.*;
import java.net.Socket;
/**
 * Classe recevant en parametre le socket d'ecoute d'un client.
 * Elle est executee dans un autre thread
 * Son but est de recevoir les messages du serveur vers le client qui la instancie
 *
 */
public class ServerListenerThread extends Thread {
		
	private Socket clientSocket;
	private BufferedReader socIn;
	
	/**
	 * Constructeur
	 * @param s Socket d'ecoute du client
	 */
	ServerListenerThread(Socket s) {
		this.clientSocket = s;
	}
	
	/**
	 * Cette methode est appelee dans Client.java pour 
	 * fermer le socket d ecoute du client.
	 */
	public void closeConnection() { 
	    try {
	        socIn.close();
	    } catch (IOException ex) {
	        System.out.println("Error closing the socket and streams");
	    }
	}

 	/**
  	* Lit les messages venant du serveur.
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