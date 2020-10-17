package stream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class Recepteur extends Thread {
   InetAddress  groupeIP;
   int port;
   String nom;
   MulticastSocket socketReception;

   Recepteur(InetAddress groupeIP, int port, String nom)  throws Exception { 
	   this.groupeIP = groupeIP;
	   this.port = port;
	   this.nom = nom;
	   socketReception = new MulticastSocket(port); //socket relié au port retenu
	   socketReception.joinGroup(groupeIP); //socket indique qu'il joint le groupe en indiquant l'adresse IP virtuelle de ce groupe
	   start();  //attendre des datagrammes par le socket
  }

  public void run() {
    DatagramPacket message;
    byte[] contenuMessage;
    String texte;
    //ByteArrayInputStream lecteur;
    
    while(true) {
		  contenuMessage = new byte[1024];
		  message = new DatagramPacket(contenuMessage, contenuMessage.length);
		  try {
	              socketReception.receive(message); //recebir mensajes desde este socket
	              
	              //Eviter que la même personne de recevoir son propre message
	              texte = (new DataInputStream(new ByteArrayInputStream(contenuMessage))).readUTF();
	       if (!texte.startsWith(nom)) continue;
	       System.out.println(texte);
		  }
		  catch(Exception exc) {
	    		System.out.println(exc);
		  }
    }
  }
}

class Emetteur extends Thread{
   InetAddress  groupeIP;
   int port;
   MulticastSocket socketEmission;
   String nom;
  
   Emetteur(InetAddress groupeIP, int port, String nom) throws Exception {
      this.groupeIP = groupeIP;
      this.port = port;
      this.nom = nom;
      socketEmission = new MulticastSocket();
      //socketEmission.setTimeToLive(15); // pour un site
      start();
  }
    
  public void run() {
    BufferedReader entreeClavier;
    
    try {
       entreeClavier = new BufferedReader(new InputStreamReader(System.in));
       while(true) {
			  String texte = entreeClavier.readLine();
			  emettre(texte);
       }
    }
    catch (Exception exc) {
       System.out.println(exc);
    }
  } 

  void emettre(String texte) throws Exception {
		byte[] contenuMessage;
		DatagramPacket message;
	
		ByteArrayOutputStream sortie = new ByteArrayOutputStream(); 
		texte = nom + " : " + texte ;
		(new DataOutputStream(sortie)).writeUTF(texte); 
		contenuMessage = sortie.toByteArray();
		message = new DatagramPacket(contenuMessage, contenuMessage.length, groupeIP, port);
		socketEmission.send(message);
  }
}

class Multicast {
   public static void main(String[] arg) throws Exception{ 
		String nom = arg[0];
		InetAddress groupeIP = InetAddress.getByName("239.255.80.84");
		int port = 8084; 
		new Emetteur(groupeIP, port, nom);
		new Recepteur(groupeIP, port, nom);
   }
}