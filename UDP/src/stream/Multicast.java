package stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 *  Il permet à un groupe de machines de communiquer d'une manière que chaque message envoyé soit reçu par l'ensemble des membres du groupe.
 *  Le groupe utilise pour communiquer une adresse IP virtuelle ; cette adresse doit nécessairement être comprise entre 224.0.1.0 et 239.255.255.255
 *  Utilise également un numéro de port qui sera le numéro de port sur lequel les messages seront attendus.
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