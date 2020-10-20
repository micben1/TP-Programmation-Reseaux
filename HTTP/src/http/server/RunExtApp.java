package http.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *  Executer une application extérieur.
 *  Attend la fin de l'execution.
 *  Récupère la réponse de l'application (écoute de son flux sortant).
 *  Ecrit la réponse de l'application dans le flux sortant vers le client
 * 
 * @author Pierre-Louis Jallerat
 *
 */

public class RunExtApp extends Thread{
	
	private BufferedOutputStream bufOut;
	private String[] command;
	
	/**
	 * 
	 * @param bufO
	 * 	Flux de sortie de données vers le client
	 * @param cmd
	 * 	Paramètre à données en entrée de l'application à executer
	 */
	public RunExtApp(BufferedOutputStream bufO, String[] cmd) {
		this.bufOut = bufO;
		this.command = cmd;
	}
	
	@Override
	public void run(){
		 Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(command);
			InputStreamReader reader = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			String result = "";
			while ( (line = br.readLine()) != null){
				result += line;
			}
			process.waitFor();
			WebServer wb = new WebServer();
			String header = wb.makeHeader("200 OK", "html");
			bufOut.write(header.getBytes());
			bufOut.write(result.getBytes());
			bufOut.flush();
			bufOut.close();
			reader.close();
		} catch (IOException ioe){
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
	  }
	}

}
