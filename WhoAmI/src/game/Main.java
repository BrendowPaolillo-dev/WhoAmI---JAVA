package game;

import java.util.concurrent.ThreadLocalRandom;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
	// General use
	private static Scanner reader = new Scanner(System.in);

	// References
	private static GameManager gameManager;
	private static Game game;

	// Variables to connection
	private static String host = "localhost";
	private static int port = 12112;
	private static int maxPlayers = 0;

	public static void Menu() {
		// TODO: Write menu prints
	}

	public static ServerSocket tryConnection() throws InterruptedException {
		while (true) {
			try {
				port = ThreadLocalRandom.current().nextInt(2000, 30000);
				return new ServerSocket(port);
			} catch (IOException e) {
				Thread.sleep(1000);
				System.out.println("Tentando conectar");
			}
		}
	}

	public static void CreateSession() throws IOException, InterruptedException {
		/**
		 * This function need to create a game host and add the creator as a player.
		 */
		System.out.println("Antes de prosseguir, por qual nome você quer ser chamado?");
		String nickname = "Daniel";

		
		ServerSocket ss = tryConnection();
		gameManager = new GameManager(ss);

		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);

		game = new Game(player);

		System.out.println("Você foi conectado na porta " + String.valueOf(port));

		Socket socket2 = ss.accept(); // Accept the local player
		gameManager.addPlayer(Utils.createPlayer(nickname, socket2));

		getMaxPlayers();
		waitPlayers(); // Request another players
	}

	public static void getMaxPlayers() {
		System.out.println("Me diga a quantidade de player para esta sessão. Intervalo [1, 10]");
		System.out.println(">");
		maxPlayers = reader.nextInt();

		while (maxPlayers <= 0 || maxPlayers > 10) {
			System.out.println("Por favor, digite um valor entre 1 e 10.");
			System.out.println(">");
			maxPlayers = reader.nextInt();
		}
	}

	public static void waitPlayers() throws IOException {
		for (int i = 0; i < maxPlayers; i++) {
			System.out.println("Esperando por outros jogadores");
			gameManager.waitForPlayer();
			System.out.println("Um jogador foi conectado");
		}
	}

	public static void SignInSession() throws UnknownHostException, IOException {
		System.out.println("Antes de proceguir, por qual nome você quer ser chamado?");
		System.out.println(">");
		String nickname = "Leinad";
		// nickname = reader.next();

		System.out.println("Para se conectar a uma sessão você deve fornecer a porta da sessão dos seus amigos.");
		System.out.println("Porta: ");
		port = reader.nextInt();

		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);

		// Ensuring that players with nicknames do not exist.
		String response = player.getMessager().receiveMessage();
		while (!response.equals("confirmed")) {
			System.out.println("Este nome já existe!");
			System.out.println("Por favor, me diga outro nome.");
			System.out.println(">");
			nickname = reader.next();
			player.setNickname(nickname);
			player.getMessager().sendMessage(nickname);
			response = player.getMessager().receiveMessage();
		}

		game = new Game(player);
		socket = new Socket(host, port);
		System.out.println("Você esta conectado");
	}

	public static void Intructions() {
		// TODO: Write instruction prints
	}

	public static void run() throws IOException, InterruptedException {
		// TODO: Create one thread to host and take the thread main to player
		if (gameManager != null) {
			Thread thread = new Thread(gameManager);
			thread.start();
		}
		game.run();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
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
