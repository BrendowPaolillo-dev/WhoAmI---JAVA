package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Messager {
	
	private Socket socket;
	private BufferedReader socketReader;
	private PrintWriter socketPrinter;
	
	public Messager(Socket socket) throws IOException {
		this.socket = socket; // Injection service, references needed to be guard
				
		OutputStream outputStream = socket.getOutputStream();
		this.socketPrinter = new PrintWriter(outputStream, true); // Reference to send messages
		
		InputStream inputStream = socket.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	    this.socketReader = new BufferedReader(inputStreamReader); // Reference to receive messages
	}

	public void sendMessage(String message) throws IOException {
		if (message.trim() != "") {
			this.socketPrinter.println(message);
			this.socketPrinter.flush();
		}
	}
	
	public String receiveMessage() throws IOException {
		String response = this.socketReader.readLine();
		while (response == null)
			response = this.socketReader.readLine();
	    return response;
	}
	
	public String toString() {
		return socket.toString();
	}
	
	public void disconnect() throws IOException {
		this.socket.close();
		this.socketPrinter.close();
		this.socketReader.close();
	}
}