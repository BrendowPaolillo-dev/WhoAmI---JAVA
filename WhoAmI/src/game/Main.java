package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
	// General use
	private static Scanner reader;

	// References
	private static Socket socket;
	private static GameHost gameHost;
	private static Game game;

	// Variables to connection
	private static String host = "localhost";
	private static int port = 12112;

	public static void Menu() {
		// TODO: Write menu prints
	}

	public static void CreateSession() throws IOException {
		/**
		 * This function need to create a game host and add the creator as a player.
		 */
		System.out.println("Antes de proceguir, por qual nome você quer ser chamado?");
		String nickname = "Daniel";

		// Maybe this can be pushed into the class
		ServerSocket ss = new ServerSocket(port);
		Host h = new Host(ss);
		GameManager gm = new GameManager(h);
		gameHost = new GameHost(gm);

		socket = new Socket(host, port);
		gameHost.createPlayer(nickname, socket);

		System.out.println("Você foi conectado na porta " + String.valueOf(port));

		waitPlayers();
	}

	public static void waitPlayers() throws IOException {
		System.out.println("Esperando por outros jogadores");
		gameHost.getGameManager().getHost().addConnection();
		System.out.println("Um jogador foi conectado");
	}

	public static void SignInSession() throws UnknownHostException, IOException {
		System.out.println("Antes de proceguir, por qual nome você quer ser chamado?");
		String nickname = "Leinad";

		System.out.println("Para se conectar a uma sessão você deve fornecer a porta da sessão dos seus amigos.");
		System.out.println("Porta: ");
		// port = reader.nextInt();

		game = new Game();
		socket = new Socket(host, port);
		game.addPlayer(nickname, socket);
		System.out.println("Você esta conectado");
	}

	public static void Intructions() {
		// TODO: Write instruction prints
	}

	public static void run() throws IOException, InterruptedException {
		if (game != null)
			game.run();
		else
			gameHost.run();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		reader = new Scanner(System.in);
		System.out.println("Enter C or E: ");
		char option = reader.next(".").charAt(0);

		switch (option) {
		case 'e':
		case 'E':
			SignInSession();
			break;
		case 'c':
		case 'C':
			CreateSession();
			break;
		}
		
		run();
	}

}
