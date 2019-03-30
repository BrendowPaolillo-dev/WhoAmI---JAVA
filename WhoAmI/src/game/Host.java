package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class Host extends Observable {

	private ServerSocket serverSocket;
	private ArrayList<Messager> sockets;

	public Host(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		this.sockets = new ArrayList<Messager>();
	}

	public Messager addConnection() throws IOException {
		Socket socket = this.serverSocket.accept();
		return this.addConnection(socket);
	}
	
	public Messager addConnection(Socket socket) throws IOException {
		Messager messager = new Messager(socket);
		this.sockets.add(messager);
		return messager;
	}

	public void broadcast(Messager messager, String message) {
		this.sockets.forEach(m -> {
			if (!messager.equals(m)) {
				try {
					m.sendMessage(message);
				} catch (IOException e) {
					this.sockets.remove(m);
					this.setChanged();
					this.notifyObservers(m);
				}
			}
		});
	}

}
