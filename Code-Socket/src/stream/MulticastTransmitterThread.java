package stream;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastTransmitterThread extends Thread{
	
		   InetAddress  groupIP;
		   int port;
		   MulticastSocket socketEmission;
		   String name;
		  
		   MulticastTransmitterThread(InetAddress groupIP, int port, String name) throws Exception {
		      this.groupIP = groupIP;
		      this.port = port;
		      this.name = name;
		      socketEmission = new MulticastSocket();
		      start();
		  }
		    
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

