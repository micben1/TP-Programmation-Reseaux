package stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 *  Il permet � un groupe de machines de communiquer d'une mani�re que chaque message envoy� soit re�u par l'ensemble des membres du groupe.
 *  Le groupe utilise pour communiquer une adresse IP virtuelle ; cette adresse doit n�cessairement �tre comprise entre 224.0.1.0 et 239.255.255.255
 *  Utilise �galement un num�ro de port qui sera le num�ro de port sur lequel les messages seront attendus.
 * @author Mike Bensaid et Pierre-Louis
 *
 */

public class Multicast {

	public static void main(String[] arg) throws Exception{ 
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Entrez votre nom d'utilisateur: ");
		String name = stdIn.readLine();
		InetAddress groupIP = InetAddress.getByName("224.6.6.6");
		int port = 8088; 
		/** Le thread en charge de recevoir du message*/
		new MulticastReceiverThread(groupIP, port);
		/** Le thread en charge de l'envoi du message*/
		new MulticastTransmitterThread(groupIP, port, name);
	}
}