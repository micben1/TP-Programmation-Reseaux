package stream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *  Envoyer des messages au groupe
 * @author Mike Bensaid et Pierre-Louis
 *
 */

public class MulticastTransmitterThread extends Thread{
	       /**IP adresse de groupe*/
		   InetAddress  groupIP;
		   /**le port de réception*/
		   int port;
		   /**Socket Multicast abonné au groupe*/
		   MulticastSocket socketEmission;
		   /**Le nom de l'émetteur*/
		   String name;
		   
		   /**
		    * Création d'une socket associée au numéro de port de réception
		    * attendre des datagrammes par le socket
		    * @param groupIP IP utilisée par le groupe pour communiquer
		    * @param port port port sur lequel les messages seront envoyé
		    * @param name Le nom de l'émetteur
		    * @throws Exception
		    */
		  
		   MulticastTransmitterThread(InetAddress groupIP, int port, String name) throws Exception {
		      this.groupIP = groupIP;
		      this.port = port;
		      this.name = name;
		      socketEmission = new MulticastSocket();
		      start();
		  }
		   
		   /**
		    * Boucle principale du processus
		    * Ouverture des flux de lecture a partir de system
		    * Faites passer le message
		    */
		    
		  public void run() {
		    BufferedReader keyboardIn;
		    
		    try {
		       keyboardIn = new BufferedReader(new InputStreamReader(System.in));
		       while(true) {
					  String text = keyboardIn.readLine();
					  transmit(text);
		       }
		    }
		    catch (Exception e) {
		       System.out.println(e);
		    }
		  } 
		  
		  /**
		   * Ouverture des flux d'écriture
		   * Création de datagamPacket 
		   * Et envoyer le message via le socket
		   * @param text Le message à transmettre
		   * @throws Exception
		   */

		  void transmit(String text) throws Exception {
				byte[] messageContent;
				DatagramPacket message;
			
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				text = name + " : " + text ;
				(new DataOutputStream(out)).writeBytes(text);
				messageContent = out.toByteArray();
				message = new DatagramPacket(messageContent, messageContent.length, groupIP, port);
				socketEmission.send(message);
				
		  }
		
 }

