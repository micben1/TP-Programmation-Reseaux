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
		   /**le port de r�ception*/
		   int port;
		   /**Socket Multicast abonn� au groupe*/
		   MulticastSocket socketEmission;
		   /**Le nom de l'�metteur*/
		   String name;
		   
		   /**
		    * Cr�ation d'une socket associ�e au num�ro de port de r�ception
		    * attendre des datagrammes par le socket
		    * @param groupIP IP utilis�e par le groupe pour communiquer
		    * @param port port port sur lequel les messages seront envoy�
		    * @param name Le nom de l'�metteur
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
		   * Ouverture des flux d'�criture
		   * Cr�ation de datagamPacket 
		   * Et envoyer le message via le socket
		   * @param text Le message � transmettre
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

