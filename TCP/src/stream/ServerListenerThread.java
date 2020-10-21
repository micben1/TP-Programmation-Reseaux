/**
 * ServerListenerThread
 * @author Pierre-Louis Jallerat et Mickael Bensaid
 */
package stream;

import java.io.*;
import java.net.Socket;
/**
 * Classe recevant en parametre le flux d'ecoute du socket d'un client.
 * Elle est executee dans un autre thread
 * Son but est de recevoir les messages du serveur vers le client qui la instancie
 *
 */
public class ServerListenerThread extends Thread {
		
	private BufferedReader socIn;
	
	/**
	 * Constructeur
	 * @param socIn flux d'ecoute du socket
	 */
	ServerListenerThread(BufferedReader socIn) {
		this.socIn = socIn;
	}

 	/**
  	* Lit les messages venant du serveur.
  	**/
	public void run() {
    	  try {
    		while (true) {
    		  String line = socIn.readLine();
    		  System.out.println(line);
    		}
    	} catch (Exception e) {
        	System.err.println("Error in clientSocket:" + e); 
        }
       }

}