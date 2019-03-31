package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestMessager {
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(6000);
		
		Socket socket = new Socket("localhost", 6000);
		
		ss.accept();
		
		Messager messager = new Messager(socket);
		
		
		messager.sendMessage("Ola mundo");
		
		String response;
		
		while ( (response = messager.receiveMessage()) == null) ;
		
		System.out.println(response);
	}
}
