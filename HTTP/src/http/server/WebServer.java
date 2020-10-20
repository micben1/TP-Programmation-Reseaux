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
 * WebServer est serveur web gérant differente requete (GET, POST, DELETE, etc.)
 * permettant d'acceder ou d'ajouter des ressources au serveur
 * 
 * @author Mickeal Bensaid et Pierre-Louis JALLERAT
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

				//Gère le premier message nul
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
	/**
	 * Lis le contenu de l'entete et extrait les informations utiles.

	 * @param header
	 * 	String contenant l'integralite du header
	 * @param headerInfo
	 * 	Tableau rempli par les informations utiles contenu dans l'entete
	 *	0: la methode (GET, PUT, etc.)
	 *	1: l'URL 
	 *	2: la taille du corps de la requete
	 *	3: le type de contenu du corps de la requete
	 */
	void readHeader (String header, String[] headerInfo) {
		if (header != null && header.length() > 0) {
			String arr[] = header.split("[ \n]");
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

	/**
	 * Lis le contenu du corps de la requete.
	 * 
	 * @param in
	 * 	Le flux de donnée entrante donnant accès aux corps de la requete.
	 * @param length
	 * 	La longueur du corps de la requete.
	 * @return
	 * 	Corps de la requete sous forme de String.
	 */
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

	/**
	 * Traite les requete HTTP de type HEAD et repond au client web
	 * La methode HEAD demande les en-têtes qui seraient retournés 
	 * si la ressource specifiee etait demandée avec une methode HTTP GET. 
	 * 
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web)?
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 */
	void httpHEAD(BufferedOutputStream bufOut, String url) {
		//supprimer les flush ?
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
	
	/**
	 * Traite les requetes HTTP de type GET et repond au client web
	 * La methode GET demande une représentation de la ressource specifiee. 
	 * Elle uniquement être utilisees afin de récupérer des donnees.
	 * 
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web).
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 */
	void httpGET(BufferedOutputStream bufOut, String url){
		//supprimer flush ?
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
	/**
	 * Traite les requetes DELETE et répond au client web
	 * La methode DELETE supprime la ressource indiquee.
	 * 
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web).
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 */
	void httpDELETE(BufferedOutputStream bufOut, String url) {
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

	/**
	 * Traite les requetes PUT et répond au client web.
	 * La methode PUT cree une nouvelle ressource ou remplace une representation 
	 * de la ressource ciblee par le contenu de la requete.
	 * 
	 * @param in
	 * 	Flux de donnees entrant (depuis le client web).
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web).
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 * @param bodyLength
	 * 	Taille du corps de la requete à ecrire dans une ressource serveur.
	 */
	void httpPUT(BufferedReader in, BufferedOutputStream bufOut, String url, int bodyLength) {
		
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
	
	/**
	 * Traite les requetes POST et repond au client web.
	 * La methode POST envoie des donnees au serveur. 
	 * Le type du corps de la requete est indiqué par l'entete Content-Type.
	 * @param in
	 * 	Flux de donnees entrant (depuis le client web).
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web).
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 * @param bodyLength
	 * 	Taille du corps de la requete a ecrire dans une ressource serveur.
	 * @param bodyContent
	 * 	Type des donnees envoyees par le client.
	 */
	void httpPOST(BufferedReader in, BufferedOutputStream bufOut, String url, int bodyLength, String bodyContent) {
		
		/*
		 * Amélioration: Lecture des fichiers audio, vidéo et images.
		 * Actuellement bodyContent est inutile
		 */
		//Vérifier si extension est txt ou html
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

	/**
	 * Cette methode lit le fichier en parametre et ecrit les donnees associees 
	 * dans le flux de sortie (vers le client)
	 * @param ressource
	 * 	Fichier que l'on souhaite renvoye au client
	 * @param bufOut
	 * 	Flux de donnees sortant (vers le client web).
	 * @param url
	 * 	parametre de la requete (localisation de la ressource).
	 */
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

	/**
	 * Ecrit l'entete de la réponse à renvoyer au client en fonction du status de la requete
	 * et, s'il y a un fichier à renvoyer, de son extension
	 * @param status Code de status HTTP de la requete à renvoyer
	 * @param extension Si la requete ne renvoit pas de fichier, extension = null sinon extension contient l'extension du fichier
	 * @return L'entete à renvoyer au client
	 */
	String makeHeader(String status, String extension) {
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
	 * Execute l'application Calculator dans un nouveau thread. L'application Calculator
	 * attend en parametre 2 nombres, les multiplie et renvoit le resultat.
	 * @param bufOut Flux de donnees sortant (vers le client web).
	 * @param urlParams Contient les parametres d'execution de l'application Calculator
	 */
	void runCalculatorApp(BufferedOutputStream bufOut, String urlParams) {
		HashMap<String, String> params = readParams(bufOut, urlParams);
		
		if (params != null && params.size() > 1) {
			String[] command = new String [3];
			command[0] = "C:\\Users\\jalle\\Travail\\Reseaux\\TP1\\TP-Progrmation-Reseaux\\HTTP\\lib\\Calculator.exe";
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

	/**
	 * Analyse l'URL de la requete et renvoit une collection chaque parametre
	 * et sa valeur associee
	 * @param bufOut Flux de donnees sortant (vers le client web).
	 * @param urlParams URL de la requete émise pas le client
	 * @return Chaque "key" contient le nom du parametre et "value" contient la valeur associe.
	 */
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
