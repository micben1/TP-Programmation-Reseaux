///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    System.out.println("Webserver starting up on port 3000");
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
        boolean msgNull = false;
        while (str != null && !str.equals("")) {
        		str = in.readLine();
        	if (str == null) {
        		msgNull = true;
        		break;
        	} else {
        	completeMessage  += str + "\n";
        	}
        }

        if (!msgNull) {
        	String[] headerInfo = new String[2];
            readHeader(completeMessage, headerInfo);
            String method = headerInfo[0];
            String url = headerInfo[1];
            
            switch(method) {
            case "GET":
            	httpGET(bufOut, url);
            	break;
            case "HEAD":
            	httpHEAD(bufOut, url);
            	break;
            case "DELETE":
                httpDELETE(bufOut, url);
                break;
            case "PUT":
            	httpPUT(bufOut, url);
            	break;
            case "POST":
            	httpPOST(bufOut, url);
            	break;
            default:
            	System.out.println("method pas implemente");
            }
        }
        
		bufOut.close();

        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }
 


private void httpPOST(BufferedOutputStream bufOut, String url) {
	
	
}



private void httpPUT(BufferedOutputStream bufOut, String url) {
	String content = "<h1>teseeeeeeeeeeeeet<h1>";
	String header =  "";
	  String arr[] = url.split("\\.");
	  String extension = arr[1];
	  File ressource = new File("./ressources/" + url);
	  try {
		  
		  PrintWriter dc = new PrintWriter(ressource); //Eface le continue de fichier avant
		  dc.close();
		  
		  bufOut.write(header.getBytes());
		  writeFileInBufOut(ressource, bufOut, "./ressources/" + url);
		  bufOut.flush();
		 
		  
		  if (ressource.exists() && ressource.isFile()) {
			  header = makeHeader("403 No Content", extension);
			  bufOut.write(header.getBytes());
			  bufOut.write(content.getBytes());
			  bufOut.flush();
		  } else {
			  header = makeHeader("401 Created", null);
			  bufOut.write(header.getBytes());
			  bufOut.write(content.getBytes());
			  bufOut.flush();
		  }
	  } catch(Exception e) {
		  try {
			  header = makeHeader("500 Internal Server Error", null);
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  } catch (Exception e2) {}
	  }
}
	



void writeFileInBufOut (File ressource, BufferedOutputStream bufOut, String url) {
	  try {      
			BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(ressource));
			byte[] buffer = new byte[256];
			int nbRead;
			while((nbRead = fileIn.read(buffer)) != -1) {
				bufOut.write(buffer, 0, nbRead);
			}
			fileIn.close();
	  } catch (IOException e) {
		  e.printStackTrace();
			try {
				String header = makeHeader("500 Internal Server Error", null);
				bufOut.write(header.getBytes());
				bufOut.flush();
			} catch (Exception e2) {};
	  }
  }
  
  void readHeader (String completeMsg, String[] headerInfo) {
	  if (completeMsg != null && completeMsg.length() > 0) {
		  String arr[] = completeMsg.split(" ");
		  //method
		  headerInfo[0] = arr[0];
		  //url
		  headerInfo[1] = arr[1].substring(1,arr[1].length());
		  //System.out.println("method: " + headerInfo[0]);
		  //System.out.println("url: " +  headerInfo[1]);
		  //System.out.println();
		  //System.out.println(completeMsg);
		  //System.out.println();
		  //String[] a = completeMsg.split("\n\\s*\n");
		  //System.out.println("header: " + a[0]);
		  //System.out.println("body: " +  a[1]);
	  }

  }
  
  void readBody () {
	  
  }
  
  public void httpGET(BufferedOutputStream bufOut, String url){
	  String header =  "";
	  String arr[] = url.split("\\.");
	  String extension = arr[1];
	  File ressource = new File("./ressources/" + url);
	  try {
		  if (ressource.exists() && ressource.isFile()) {
			  header = makeHeader("200 OK", extension);
			  bufOut.write(header.getBytes());
			  writeFileInBufOut(ressource, bufOut, "./ressources/" + url);
			  bufOut.flush();
		  } else {
			  header = makeHeader("404 not Found", null);
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  }
	  } catch(Exception e) {
		  try {
			  header = makeHeader("500 Internal Server Error", null);
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  } catch (Exception e2) {}
	  }
  }
  
  private void httpDELETE(BufferedOutputStream bufOut, String url) {
		String header =  "";
		  String arr[] = url.split("\\.");
		  String extension = arr[1];
		  File ressource = new File("./ressources/" + url);
		  try {
			  if (ressource.exists() && ressource.isFile()) {
				  ressource.delete();
				  header = makeHeader("200 OK", extension);
				  bufOut.write(header.getBytes());
				  bufOut.flush();
			  } else if (!ressource.exists()){
				  header = makeHeader("404 not Found", null);
				  bufOut.write(header.getBytes());
				  bufOut.flush();
			  } else {
				  header = makeHeader("403 Forbidden", null);
				  bufOut.write(header.getBytes());
				  bufOut.flush(); 
			  }  
		  } catch(Exception e) {
			  try {
				  header = makeHeader("500 Internal Server Error", null);
				  bufOut.write(header.getBytes());
			  } catch (Exception e2) {}
		  }
	}
  
  private void httpHEAD(BufferedOutputStream bufOut, String url) {
	  String header =  "";
	  String arr[] = url.split("\\.");
	  String extension = arr[1];
	  File ressource = new File("./ressources/" + url);
	  try {
		  if (ressource.exists() && ressource.isFile()) {
			  header = makeHeader("200 OK", extension);
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  } else {
			  header = makeHeader("404 not Found", null);
			  bufOut.write(header.getBytes());
			  bufOut.flush();
		  }
	  } catch(Exception e) {
		  try {
			  header = makeHeader("500 Internal Server Error", null);
			  bufOut.write(header.getBytes());
		  } catch (Exception e2) {}
	  }
  }
  
  public String makeHeader(String status, String extension) {
	  String header = "HTTP/1.0 " + status + "\r\n";
	  String type = "";
	  if (extension != null) {
		  // transformer en hash map et g�rer les erreurs fichier audio s'il y a temps
		  if (extension.equals("htm") || extension.equals("html")) type = "text/html";
		  else if (extension.equals("mp3")) type = "audio/mpeg";
		  else if (extension.equals("mp4")) type = "vidéo/mp4";
		  else if (extension.equals("avi")) type = "video/x-msvideo";
		  else if (extension.equals("css")) type = "text/css";
		  else if (extension.equals("csv")) type = "text/csv";
		  else if (extension.equals("gif")) type = "image/gif";
		  else if (extension.equals("aac")) type = "audio/aac";  
		  else if (extension.equals("jpeg") || extension.equals("jpg")) type = "image/jpeg";
		  else if (extension.equals("json")) type = "application/json";
		  else if (extension.equals("pdf")) type = "application/pdf";
		  else if (extension.equals("png")) type = "image/png";
		  else if (extension.equals("xhtml")) type = "application/xhtml+xml";
		  else if (extension.equals("xml")) type = "application/xml";
		  else if (extension.equals("png")) type = "image/png";
		  else type = "application/octet-stream";
		  
		  header += "Content-Type: "+ type +"\r\n";
	  }
	  header += "Server: Bot\r\n";
	  // this blank line signals the end of the headers
	  header += "\r\n";
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