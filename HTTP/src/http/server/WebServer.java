///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    for (;;) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
            remote.getInputStream()));
        // PrintWriter out = new PrintWriter(remote.getOutputStream());
	    BufferedOutputStream bufOut = new BufferedOutputStream(remote.getOutputStream());

        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        String str = ".";
        String completeMessage = "";
        while (str != null && !str.equals("")) {
        	str = in.readLine();
        	if (str ==  null) break;
        	completeMessage  += str + "\n";
        }
		
        readHeader(completeMessage, bufOut);
        
		bufOut.close();

        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }
  
  void writeFileInBufOut (File ressource, BufferedOutputStream bufOut, String url) {
	  try {      
			BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(ressource));
			// Envoi du corps : le fichier (page HTML, image, vid�o...)
			byte[] buffer = new byte[256];
			int nbRead;
			while((nbRead = fileIn.read(buffer)) != -1) {
				bufOut.write(buffer, 0, nbRead);
			}
			// Fermeture du flux de lecture
			fileIn.close();
	  } catch (IOException e) {
		  e.printStackTrace();
			try {
				bufOut.write(makeHeader("500 Internal Server Error").getBytes());
				bufOut.flush();
			} catch (Exception e2) {};
	  }
  }
  
  void readHeader (String header, BufferedOutputStream bufOut) {
	  String method = "";
	  String url = "";
	  if (header != null && header.length() > 0) {
		  String arr[] = header.split(" ");
		  method = arr[0];
		  url = arr[1].substring(1,arr[1].length());
		  System.out.println("method: " + method);
		  System.out.println("url: " + url);
	  }

	  switch(method) {
	  	case "GET":
		  httpGET(bufOut, url);
		  break;
		default:
			System.out.println("error");
	  }
  }
  
  public void httpGET(BufferedOutputStream bufOut, String url){
	  String header =  "";
	  File ressource = new File("./ressources/" + url);
	  try {
		  if (ressource.exists() && ressource.isFile()) {
			  header = makeHeader("200 OK");
			  bufOut.write(header.getBytes());
			  writeFileInBufOut(ressource, bufOut, "./ressources/" + url);
			  bufOut.flush();
			
		  } else {
			  header = makeHeader("404 not Found");
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  }
	  } catch(Exception e) {
		  header = makeHeader("500 Internal Server Error");
	  }
	  
  }
  
  public String makeHeader(String status) {
	  String header = "HTTP/1.0 " + status + "\r\n";
	  header += "Content-Type: text/html\r\n";
	  header += "Server: Bot\r\n";
	  // this blank line signals the end of the headers
	  header += "\r\n";
	  System.out.println(header);
	  return header;
  }
  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();
  }
}