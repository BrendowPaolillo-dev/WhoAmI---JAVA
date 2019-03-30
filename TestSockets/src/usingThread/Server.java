package usingThread;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws Exception {
		System.out.println("Server init");
		ServerSocket m_ServerSocket = new ServerSocket(12112);
		int id = 0;
		while (true) {
			Socket clientSocket = m_ServerSocket.accept();
			Client cliThread = new Client(clientSocket, id++);
			System.out.println("Client enter");
			cliThread.start();
		}
	}
}