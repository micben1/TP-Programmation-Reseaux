package stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiverThread extends Thread {
	   InetAddress  groupIP;
	   int port;
	   String name;
	   MulticastSocket socketReception;

	   MulticastReceiverThread(InetAddress groupIP, int port)  throws Exception { 
		   this.groupIP = groupIP;
		   this.port = port;
		   socketReception = new MulticastSocket(port); //socket relié au port retenu
		   socketReception.joinGroup(groupIP); //socket indique qu'il joint le groupe en indiquant l'adresse IP virtuelle de ce groupe
		   start();  //attendre des datagrammes par le socket
	  }

	  public void run() {
	    DatagramPacket message;
	    byte[] messageContent;
	    String text;
	    
	    while(true) {
	    	messageContent = new byte[256];
			  message = new DatagramPacket(messageContent, messageContent.length);
			  try {
		        socketReception.receive(message); //recebir mensajes desde este socket
		              
		              
		        text = (new DataInputStream(new ByteArrayInputStream(messageContent))).readLine();
		       System.out.println(text);
			  }
			  catch(Exception e) {
		    		System.out.println(e);
			  }
	    }
	  }
	}
