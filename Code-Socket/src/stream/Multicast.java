package stream;

import java.io.*;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Multicast {
   public static void main(String[] arg) throws Exception{ 
	   	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	   	System.out.print("Entrez votre nom d'utilisateur: ");
	   	String name = stdIn.readLine();
		InetAddress groupeIP = InetAddress.getByName("224.6.6.6");
		int port = 8088; 
		new MulticastReceiverThread(groupeIP, port);
		new MulticastTransmitterThread(groupeIP, port, name);
   }
}