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
    		while (true) {
    		  String line = socIn.readLine();
    		  System.out.println(line);
    		  sendMsg(line, socOut);
    		}
    	} catch (Exception e) {
        	System.err.println("Error in EchoServer:" + e); 
        }
       }
  
  }