/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package stream;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.net.*;

public class ClientThread
	extends Thread {
	
	private Socket clientSocket;
	public static List<PrintStream> socOutList = Collections.synchronizedList(new ArrayList<PrintStream>());
	String name = "";
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

	static void sendMsg(String line, PrintStream currentSoc) {
	  for(int i = 0; i < socOutList.size(); i ++) {
		  if (socOutList.get(i) != currentSoc) {
			  socOutList.get(i).println(line);
		  }
	  }
     }
 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
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
    			  line = name + " s'est connecté(e) !";
    			  System.out.println(line);
    			  sendMsg(line, socOut);
    		  } else if (line.equals(".") || line == null) {
    			  System.out.println(name + "s'est déconnecté(e)");
    			  sendMsg(name + " s'est déconnecté(e)", socOut);
    			  break;
    		  } else {
    			  System.out.println(line);
    			  sendMsg(name + ": " + line, socOut);
    		  }
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }