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
	public static String historic = "";
	String name = "";
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

	static void sendMsg(String line, PrintStream currentSocOut) {
	  for(int i = 0; i < socOutList.size(); i ++) {
		  if (socOutList.get(i) != currentSocOut) {
			  socOutList.get(i).println(line);
		  }
	  }
     }
	static void deleteSoc(PrintStream socOut) {
	  for(int i = 0; i < socOutList.size(); i ++) {
		  if (socOutList.get(i) == socOut) {
			  socOutList.remove(i);
		  }
	  }
	}
	static void sendHistoric(PrintStream currentSocOut) {
		if (historic.length() > 0) {
			System.out.println("historic");
			currentSocOut.print(historic);
		}
				
	}
	
	void updateHistoric(String line, PrintWriter pw) {
	  pw.write(line + "\n");
	  pw.flush();
	  historic += line + "\n";
	}
	
 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	public void run() {
    	  try {
	    	FileWriter fileWriter = new FileWriter("./Code-Socket/files/historic.txt", true);
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
    			  line = name + " s'est connecté(e) !";
    			  System.out.println(line);
    			  sendMsg(line, socOut);
    			  sendHistoric(socOut);
    			  updateHistoric(line, pw);
    		  } else if (line.equals(".") || line == null) {
    			  System.out.println(name + " s'est déconnecté(e)");
    			  sendMsg(name + " s'est déconnecté(e)", socOut);
    			  updateHistoric(name + " s'est déconnecté(e)", pw);
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