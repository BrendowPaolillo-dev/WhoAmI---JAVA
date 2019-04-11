package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

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

	private static char option;

	public static void Menu() throws UnknownHostException, IOException, InterruptedException {
		Utils.printr("=");
		Utils.print("Quem eu sou?");
		Utils.printr("=");
		Utils.print("(C)riar sessão");
		Utils.print("(E)ntrar em uma sessão");
		Utils.print("(I)nstruções");
		Utils.print("(S)air");
		System.out.println();
		Utils.printIn();

		option = reader.nextLine().charAt(0);
		Utils.printr("=");
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
			Utils.print("Por favor, digite uma das opções acima!");
			break;
		}
	}

	public static void CreateSession() throws IOException, InterruptedException {
		Utils.print("Antes de prosseguir, por qual nome você quer ser chamado?");
		System.out.println();
		Utils.printIn();
		
		String nickname = reader.nextLine();
		System.out.println();

		// Creating the gameManager
		ServerSocket ss = tryConnection();
		gameManager = new GameManager(ss);

		// Create the first player
		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);
		game = new Game(player);

		Utils.print("Você foi conectado na porta (" + String.valueOf(port) + ")\n");

		// Force the accept for my self socket connection
		Socket socket2 = ss.accept(); // Accept the local player
		gameManager.addPlayer(Utils.createPlayer(nickname, socket2));

		getMaxPlayers(); // Put the number of players
	}

	public static ServerSocket tryConnection() throws InterruptedException {
		while (true) {
			try {
				port = ThreadLocalRandom.current().nextInt(10000, 60000);
				return new ServerSocket(port);
			} catch (IOException e) {
				Thread.sleep(100);
				Utils.print("Tentando conectar...");
			}
		}
	}

	public static void getMaxPlayers() {
		Utils.print("Me diga a quantidade de player para esta sessão.");
		Utils.print("Intervalo [2, 10].");
		System.out.println();
		Utils.printIn();
		
		maxPlayers = reader.nextInt();
		gameManager.setMaxPlayers(maxPlayers);

		while (maxPlayers <= 1 || maxPlayers > 10) {
			Utils.print("ERRO: Por favor, digite um valor entre 2 e 10.");
			System.out.println();
			Utils.printIn();
			
			maxPlayers = reader.nextInt();
			gameManager.setMaxPlayers(maxPlayers);
		}
		
		Utils.print("Esperando pelos jogadores...");
	}

	public static void SignInSession() throws UnknownHostException, IOException {
		Utils.print("Para se conectar a uma sessão você deve fornecer a");
		Utils.print("(porta) da sessão dos seus amigos.");
		System.out.println();
		Utils.printIn();
		
		port = reader.nextInt();
		
		System.out.println();
		Utils.print("Antes de prosseguir, por qual nome você quer ser chamado?");
		Utils.printIn();
		
		String nickname = reader.next();
		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);
		player.getMessager().sendMessage(nickname);
		String response = player.getMessager().receiveMessage();

		while (!response.equals("ok")) {
			Utils.print("ERRO: Este nome já existe, me diga outro.");
			System.out.println();
			Utils.printIn();
			nickname = reader.next();
			player.setNickname(nickname);
			player.getMessager().sendMessage(nickname);
			response = player.getMessager().receiveMessage();
		}
		game = new Game(player);
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
		String options = "ceisCEIS";
		do {
			Menu();
		} while (!options.contains(String.valueOf(option)));
		run();
	}

}
