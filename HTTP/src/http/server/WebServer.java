///A Simple Web Server (WebServer.java)

package http.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Server Web TP Réseau INSA Lyon
 * 2020
 * 
 * WebServer est serveur web gérant différente requête (GET, POST, DELETE, etc.)
 * permettant d'accèder ou d'ajouter des ressources au serveur
 * 
 * @author Mickeal Bensaïd & Pierre-Louis JALLERAT
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
				String header = "";
				while (str != null && !str.isEmpty()) {
					str = in.readLine();
					header  += str + "\n";
				}

				//Gère le premier msg nul
				if (!header.substring(0, 4).equals("null") && header.length() > 5) {
					String[] headerInfo = new String[4];
					/*
		        	headerInfo:
		        	0: method (GET, PUT, etc.)
		        	1: URL
		        	2: body length
		        	3: body content
					 */
					readHeader(header, headerInfo);
					String method = headerInfo[0];
					String url = headerInfo[1];
					//int bodyLength = Integer.parseInt(headerInfo[2]);

					//for (int i = 0; i < 4; i++) {
					//	System.out.println(headerInfo[i]);
					//}
					//String body = readBody(in, bodyLength);
					//System.out.println(body);
					int bodyLength;
		            switch(method) {
		            case "GET":
		            	httpGET(bufOut, url);
		            	break;
		            case "HEAD":
		            	httpHEAD(bufOut, url);
		            	bufOut.close();
		            	remote.close();
		            	break;
		            case "DELETE":
		                httpDELETE(bufOut, url);
		                bufOut.close();
		                remote.close();
		                break;
		            case "PUT":
		            	bodyLength = Integer.parseInt(headerInfo[2]);
		            	httpPUT(in, bufOut, url, bodyLength);
		            	bufOut.close();
		            	remote.close();
		            	break;
		            case "POST":
		            	String bodyContent = headerInfo[3];
		            	bodyLength = Integer.parseInt(headerInfo[2]);
		            	httpPOST(in, bufOut, url, bodyLength, bodyContent);
		            	bufOut.close();
		            	remote.close();
		            	break;
		            default:
		            	System.out.println("method pas implemente");
		            }
				}
				// remote.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error: " + e);
			}
		}
	}

	void readHeader (String completeMsg, String[] headerInfo) {
		if (completeMsg != null && completeMsg.length() > 0) {
			String arr[] = completeMsg.split("[ \n]");
			//method
			headerInfo[0] = arr[0];
			//url
			headerInfo[1] = arr[1].substring(1,arr[1].length());
			for (int u = 0; u<arr.length; u++) {
				if (arr[u].indexOf("Length") != -1) {
					headerInfo[2] = arr[u+1];
				}
				if (arr[u].indexOf("Type") != -1) {
					headerInfo[3] = arr[u+1];
				}
			}
		}
	}

	String readBody (BufferedReader in, int length) {
		String body = "";
		try {
			char buff[] = new char[length];
			in.read(buff, 0, length);
			body = new String(buff);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return body;
	}

	//supprimer les flush ?
	void httpHEAD(BufferedOutputStream bufOut, String url) {
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
			bufOut.close();
		} catch(Exception e) {
			e.printStackTrace();
			try {
				header = makeHeader("500 Internal Server Error", null);
				bufOut.write(header.getBytes());
				bufOut.close();
			} catch (Exception e2) {}
		}
	}
	
	//supprimer flush ?
	void httpGET(BufferedOutputStream bufOut, String url){
		String header =  "";
		// Vérifie si la ressource demandé n'a pas d'extension
		String arr[];
		String extension = "";
		try {
			arr = url.split("\\.");
			if (arr.length > 1) extension = arr[1];
		} catch (Exception e) {
			e.printStackTrace();
			try {
				header = makeHeader("400 Bad Request", null);
				bufOut.write(header.getBytes());
				bufOut.flush();
			} catch (Exception e2) {}  
		}
		//*******************

		if(url.substring(0, 4).equals("app?")) {
			//si c'est app demandée
			String s[] = url.split("\\?");
			if (s.length > 1) {
				// si s[1] existe
				runCalculatorApp(bufOut, s[1]);
			} else {
				runCalculatorApp(bufOut, null);
			}
		} else {
			//si c'est une ressource
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
				bufOut.close();
			} catch(Exception e) {
				e.printStackTrace();
				try {
					header = makeHeader("500 Internal Server Error", null);
					bufOut.write(header.getBytes());
					bufOut.flush();
					bufOut.close();
				} catch (Exception e2) {}
			}
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
			bufOut.close();
		} catch(Exception e) {
			e.printStackTrace();
			try {
				header = makeHeader("500 Internal Server Error", null);
				bufOut.write(header.getBytes());
				bufOut.close();
			} catch (Exception e2) {}
		}
	}

	private void httpPUT(BufferedReader in, BufferedOutputStream bufOut, String url, int bodyLength) {
		
		String header =  "";
		  String arr[] = url.split("\\.");
		  File ressource = new File("./ressources/" + url);	
		  boolean newFile = false;
	      if (ressource.exists() && ressource.isFile()) {
	    	  newFile = true;
	      }
			
		  try {
		      BufferedOutputStream OutFile = new BufferedOutputStream(new FileOutputStream(ressource)); 
		      String contenu = readBody(in, bodyLength);
		      OutFile.write(contenu.getBytes());
		      OutFile.flush();
		      OutFile.close();
			  if (newFile) {
				  header = makeHeader("200 OK", null);
				  bufOut.write(header.getBytes());
				  bufOut.flush();
			  } else {
				  header = makeHeader("201 Created", null);
				  bufOut.write(header.getBytes());
				  bufOut.flush();
			  }
		  } catch(Exception e) {
			  e.printStackTrace();
			  try {
				  header = makeHeader("500 Internal Server Error", null);
				  bufOut.write(header.getBytes());
				  bufOut.flush();
			  } catch (Exception e2) {}
		  }
	}
	
	//Vérifier si extension est txt ou html
	private void httpPOST(BufferedReader in, BufferedOutputStream bufOut, String url, int bodyLength, String bodyContent) {
		String header =  "";
		//String arr[] = url.split("\\.");
		File ressource = new File("./ressources/" + url);	
		boolean newFile = false;
		if (ressource.exists() && ressource.isFile()) {
			newFile = true;
		}

		try {
	    	FileWriter fileWriter = new FileWriter("./ressources/" + url, true);
	    	BufferedWriter bw = new BufferedWriter(fileWriter);
			PrintWriter pw = new PrintWriter(bw);
			String contenu = readBody(in, bodyLength);
			pw.write(contenu + "\n");
			pw.close();
			if (newFile) {
				header = makeHeader("200 OK", null);
				bufOut.write(header.getBytes());
				bufOut.flush();
			} else {
				header = makeHeader("201 Created", null);
				bufOut.write(header.getBytes());
				bufOut.flush();
			}
		} catch(Exception e) {
			e.printStackTrace();
			try {
				header = makeHeader("500 Internal Server Error", null);
				bufOut.write(header.getBytes());
				bufOut.flush();
			} catch (Exception e2) {}
		}
	}

	public void writeFileInBufOut (File ressource, BufferedOutputStream bufOut, String url) {
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

	void runCalculatorApp(BufferedOutputStream bufOut, String urlParams) {
		HashMap<String, String> params = readParams(bufOut, urlParams);
		
		if (params != null && params.size() > 1) {
			String[] command = new String [3];
			command[0] = "C:\\Users\\jalle\\Travail\\Reseaux\\TP1\\TP-Progrmation-Reseaux\\HTTP\\lib\\Calculatrice.exe";
			command[1] = params.get("n1");
			command[2] = params.get("n2");
			RunExtApp app = new RunExtApp(bufOut, command);
			app.start();
		} else {
			try {
				String header = makeHeader("400 Bad Request", null);
				bufOut.write(header.getBytes());
				bufOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}  
		}
	}

	HashMap<String, String> readParams(BufferedOutputStream bufOut, String urlParams) {
		if (urlParams == null || urlParams.length() < 3 ) return null;
		String paramsList [] = urlParams.split("&");
		HashMap<String, String> params = new HashMap<String, String>();
		for (String str: paramsList) {
			String as[] = str.split("=");
			if (as.length != 2) {
				return null;
			} else {
				params.put(as[0], as[1]);
			}
		}

		return params;
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
