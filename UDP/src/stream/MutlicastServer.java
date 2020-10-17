/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

public class MutlicastServer  {

	static void doService(Socket clientSocket) {

       }
  
       public static void main(String args[]){ 
    	   int groupPort = Integer.parseInt(args[0]); //port
    	   MulticastSocket multiSocket;
    	   byte[] buf = new byte[256];
        
		  	if (args.length != 1) {
		          System.out.println("Usage: java EchoServer <EchoServer port>");
		          System.exit(1);
		  	}
		try {
			InetAddress groupAddr = InetAddress.getByName("localhost"); 
			multiSocket = new MulticastSocket(groupPort);
			multiSocket.joinGroup(groupAddr);
			
			while (true) {
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            multiSocket.receive(packet);
	            String received = new String(
	              packet.getData(), 0, packet.getLength());
	            if ("end".equals(received)) {
	                break;
	            }
	        }
			multiSocket.leaveGroup(groupAddr);
			multiSocket.close();			
		
	        } catch (Exception e) {
	            System.err.println("Error in EchoServer:" + e);
	        }
       }
}

  
