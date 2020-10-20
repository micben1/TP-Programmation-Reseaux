/**
 * ClientThread
 * @author Pierre-Louis Jallerat et Mickael Bensaid
 */

package stream;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.net.*;

/**
 *
 * Cette classe est associe a chaque client. Elle dispose d'un socket d ecoute et d emission.
 * Elle dispose d'un historique des messages
 *
 */

public class ClientThread
	extends Thread {
	
	/**
	 * socOutList est une liste de flux de sortie (du serveur vers le client) partage par toutes les intenses de la classe
	 * historic est un historique ephemere partage par toutes les intenses de la classes
	 */
	private Socket clientSocket;
	public static List<PrintStream> socOutList = Collections.synchronizedList(new ArrayList<PrintStream>());
	public static String historic = "";
	String name = "";
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

	/**
	 * Envoie le message passe en parametre a tous les clients du serveur (sauf au client emetteur du message)
	 * @param line Message a envoyer
	 * @param currentSocOut flux de sortie (vers le client) du client emetteur
	 */
	static void sendMsg(String line, PrintStream currentSocOut) {
	  for(int i = 0; i < socOutList.size(); i ++) {
		  if (socOutList.get(i) != currentSocOut) {
			  socOutList.get(i).println(line);
		  }
	  }
     }
	
	/**
	 * Permet de supprimer de socOutList le flux de sortie d'un client qui s'est deconnecter
	 * @param socOut flux de sortie a supprimer de la liste partagee
	 */
	static void deleteSoc(PrintStream socOut) {
	  for(int i = 0; i < socOutList.size(); i ++) {
		  if (socOutList.get(i) == socOut) {
			  socOutList.remove(i);
		  }
	  }
	}
	
	/**
	 * Lorsque le client se connecte, cette methode lui envoit l'historique (version ephemere) de la conversation
	 * @param currentSocOut flux sortant vers le client associe a cette intense
	 */
	static void sendHistoric(PrintStream currentSocOut) {
		if (historic.length() > 0) {
			System.out.println("historic");
			currentSocOut.print(historic);
		}
				
	}
	
	/**
	 * A chaque message reuu l'historique persistant est mise a jour
	 * @param line message recu
	 * @param pw flux entrant vers le fichier texte contenant l'historique
	 */
	void updateHistoric(String line, PrintWriter pw) {
	  pw.write(line + "\n");
	  pw.flush();
	  historic += line + "\n";
	}
	
 	/**
  	* recoit un message d'un client et le renvoit aux autres clients en mettant a jour un historique
  	**/
	public void run() {
    	  try {
	    	FileWriter fileWriter = new FileWriter("./files/historic.txt", true);
	    	BufferedWriter bw = new BufferedWriter(fileWriter);
			PrintWriter pw = new PrintWriter(bw);
  			PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
  			socOutList.add(socOut);
    		BufferedReader socIn = null;
    		socIn = new BufferedReader(
    			new InputStreamReader(clientSocket.getInputStream()));    
    		String line;
    		while (true) {
    		  line = socIn.readLine();
    		  if (name.length() < 1) {
    			  name = line;
    			  line = name + " s'est connecte(e) !";
    			  System.out.println(line);
    			  sendMsg(line, socOut);
    			  sendHistoric(socOut);
    			  updateHistoric(line, pw);
    		  } else if (line.equals(".") || line == null) {
    			  System.out.println(name + " s est deconnecte(e)");
    			  sendMsg(name + " s'est deconnecte(e)", socOut);
    			  updateHistoric(name + " s est deconnecte(e)", pw);
    			  deleteSoc(socOut);
    			  pw.close();
    			  break;
    		  } else {
    			  line = name + ": " + line;
    			  System.out.println(line);
    			  sendMsg(line, socOut);
    			  updateHistoric(line, pw);
    		  }
    		}
    	} catch (SocketException exception) {
        	System.exit(1);
        	
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }