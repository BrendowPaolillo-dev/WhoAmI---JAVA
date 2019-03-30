package game;

import java.io.IOException;
import java.net.Socket;

public class Game {
	protected Player player;
	private String instruction;

	public Game() {	}
	
	public Player addPlayer(String nickname, Socket socket) throws IOException {
		Messager messager = new Messager(socket);
		Player player = new Player(nickname, messager);
		this.player = player;
		messager.sendMessage("login:" + nickname); // Request to enter the game
		return player;
	}
	
	private void waitInstructions() throws IOException {
		System.out.println("Esperando por instruções");
		instruction = player.getMessager().receiveMessage();
	}
	
	// TODO: Do procedures to make the game work
	public void run() throws IOException, InterruptedException {
		while (true)
		{
			this.waitInstructions();
			System.out.println(instruction != null ? instruction : "");
		}
	}
}
