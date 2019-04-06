package game;

import java.util.concurrent.ThreadLocalRandom;

import com.sun.org.apache.bcel.internal.generic.Instruction;

import java.io.IOException;
import java.net.InetAddress;
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
	private static String inputForm = ">> ";

	public static void Menu() throws UnknownHostException, IOException, InterruptedException {
		// TODO: Write menu prints
		System.out.println("==========================================");
		System.out.println("               Quem eu sou?               ");
		System.out.println("==========================================");
		System.out.println("              (C)riar sessão");
		System.out.println("          (E)ntrar em uma sessão");
		System.out.println("               (I)nstruções");
		System.out.println("                  (S)air");
		System.out.println();
		System.out.print(inputForm);

		char option = reader.nextLine().charAt(0);
		switch (option) {
		case 'e':
		case 'E':
			SignInSession();
			break;
		case 'c':
		case 'C':
			CreateSession();
			break;
		case 'i':
		case 'I':
			Instructions();
			break;
		case 's':
		case 'S':
			break;
		default:
			System.out.println("Por favor, digite uma das opcoes acima!");
			break;
		}
	}

	public static void CreateSession() throws IOException, InterruptedException {
		/**
		 * This function need to create a game host and add the creator as a player.
		 */
		System.out.println("Antes de prosseguir, por qual nome você quer ser chamado?");
		System.out.print(inputForm);
		String nickname = reader.nextLine();
		System.out.println();

		// Creating the gameManager
		ServerSocket ss = tryConnection();
		gameManager = new GameManager(ss);

		// Create the first player
		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);
		game = new Game(player);

		System.out.println("Você foi conectado na porta " + String.valueOf(port) + "\n");

		// Force the accept for my self socket connection
		Socket socket2 = ss.accept(); // Accept the local player
		gameManager.addPlayer(Utils.createPlayer(nickname, socket2));

		getMaxPlayers(); // Put the number of players

		waitPlayers(); // Request another players
	}

	public static ServerSocket tryConnection() throws InterruptedException {
		while (true) {
			try {
				port = ThreadLocalRandom.current().nextInt(2000, 30000);
				return new ServerSocket(port);
			} catch (IOException e) {
				Thread.sleep(100);
				System.out.println("Tentando conectar");
			}
		}
	}

	public static void getMaxPlayers() {
		System.out.println("Me diga a quantidade de player para esta sessão. Intervalo [2, 10]");
		System.out.print(inputForm);
		maxPlayers = reader.nextInt();

		while (maxPlayers <= 0 || maxPlayers > 10) {
			System.out.println("Por favor, digite um valor entre 1 e 10.");
			System.out.print(inputForm);
			maxPlayers = reader.nextInt();
		}
	}

	public static void waitPlayers() throws IOException {
		for (int i = 1; i < maxPlayers; i++) {
			System.out.println("DEBUG: Esperando por outros jogadores");
			gameManager.waitForPlayer();
		}
	}

	public static void SignInSession() throws UnknownHostException, IOException {
		System.out.println("Para se conectar a uma sessão você deve fornecer a porta da sessão dos seus amigos.");
		System.out.print("Porta: ");
		port = reader.nextInt();

		System.out.println("Antes de proceguir, por qual nome você quer ser chamado?");
		System.out.print(inputForm);
		String nickname = reader.next();

		System.out.println("DEBUG: Criando o socket do jogador externo");
		Socket socket = new Socket(host, port);
		System.out.println(socket.toString());
		if (socket.isConnected()) {
			Player player = Utils.createPlayer(nickname, socket);
			player.getMessager().sendMessage(nickname);
			String response = player.getMessager().receiveMessage();

			while (!response.equals("ok")) {
				System.out.println("Este nome já existe, me diga outro.");
				System.out.print(inputForm);
				nickname = reader.next();
				player.getMessager().sendMessage(nickname);
				response = player.getMessager().receiveMessage();
			}
			game = new Game(player);
			System.out.println("Você esta conectado");
		} else {
			System.out.println("DEBUG: SOCKET nao conectado");
		}
	}

	public static void Instructions() {
		// TODO: Write instruction prints
	}

	public static void run() throws IOException, InterruptedException {
		if (gameManager != null) {
			Thread thread = new Thread(gameManager);
			thread.start();
		}
		game.run();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Menu();
		run();
	}

}
