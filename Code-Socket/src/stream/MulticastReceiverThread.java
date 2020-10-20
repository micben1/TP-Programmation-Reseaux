package stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Recevoir les messages du groupe
 * @author Mike Bensaid et Pierre-Louis
 *
 */
public class MulticastReceiverThread extends Thread {
	   /** Adresse IP virtuelle utilis�e par le groupe pour communiquer*/
	   InetAddress  groupIP;
	   /** Le num�ro de port sur lequel les messages seront attendus*/
	   int port;
	   /**Socket Multicast abonn� au groupe*/
	   MulticastSocket socketReception;
	   
	   
       /**
        * Cr�ation d'une socket associ�e au num�ro de port, ce socket joint le groupe.
        * Apr�s cela, il attendre des datagrammes par le socket
        * @param groupIP IP utilis�e par le groupe pour communiquer
        * @param port port sur lequel les messages seront attendus
        * @throws Exception
        */
	   MulticastReceiverThread(InetAddress groupIP, int port)  throws Exception { 
		   this.groupIP = groupIP;
		   this.port = port;
		   //socket reli� au port retenu
		   socketReception = new MulticastSocket(port); 
		   //Le socket join le group 
		   socketReception.joinGroup(groupIP); 
		   //Attendre des datagrammes par le socket
		   start();  
	  }
	   
	   /**
	    * Boucle principale du processus.
	    * le recevoir des DatagramPacket ( messages)
	    * Ecouter le groupe et recevez le message
	    * 
	    */

	  public void run() {
	    DatagramPacket message;
	    byte[] messageContent;
	    String text;
	    
	    while(true) {
	    	messageContent = new byte[256];
			  message = new DatagramPacket(messageContent, messageContent.length);
			  try {
				//Recevez le message a partir du Socket  
		        socketReception.receive(message); 
		              
		        //Lire le message re�u      
		        text = (new DataInputStream(new ByteArrayInputStream(messageContent))).readLine();
		       System.out.println(text);
			  }
			  catch(Exception e) {
		    		System.out.println(e);
			  }
	    }
	  }
	}