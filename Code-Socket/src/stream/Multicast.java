package stream;

import java.net.InetAddress;

public class Multicast {
   public static void main(String[] arg) throws Exception{ 
		String name = arg[0];
		InetAddress groupeIP = InetAddress.getByName("224.6.6.6");
		int port = 8088; 
		new MulticastReceiverThread(groupeIP, port, name);
		new MulticastTransmitterThread(groupeIP, port, name);
   }
}