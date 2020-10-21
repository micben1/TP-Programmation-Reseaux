/**
 * Client
 * @author Pierre-Louis Jallerat et Mickael Bensaid
 */
package stream;

import java.io.*;
import java.net.*;
import java.net.SocketException;
/**
 * Represente un client. Cette classe instancie un socket d'emission et un socket d'ecoute.
 * Le socket d'ecoute est lance dans un autre thread via la classe ServerListenerThread
 *
 */


public class Client {

 
 /**
  * 
  * @param args 2 arguments: adresse serveur en 1er, port en 2e
  * @throws IOException execption input/output
  */
    public static void main(String[] args) throws IOException {

        Socket clientSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;
        String name = "";

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
      	    clientSocket = new Socket(args[0],new Integer(args[1]).intValue()); 
		    socOut = new PrintStream(clientSocket.getOutputStream());
		    stdIn = new BufferedReader(new InputStreamReader(System.in));
		    System.out.println("Client running");
		    
    		socIn = new BufferedReader(
        			new InputStreamReader(clientSocket.getInputStream())); 
	    
	        String line;
	        System.out.println("Pour vous deconnectez entrer '.'");
	        System.out.print("Entrez votre nom d'utilisateur: ");
	        ServerListenerThread listenerSocket = new ServerListenerThread(socIn);
	        listenerSocket.start();
	        while (true) {
	        	line=stdIn.readLine();
	        	socOut.println(line);
	         	if (line.equals(".")) {
	         		break;
	         	}
	        }
	      stdIn.close();
	      socIn.close();
   		  socOut.close();
	      clientSocket.close();
	      
        } catch (SocketException exception) {
        	System.out.println("deconnecte(e)");
        	System.exit(1);
        	
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to:"+ args[0]);
            System.exit(1);
        }
                             
    }
}