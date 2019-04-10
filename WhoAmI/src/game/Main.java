package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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

	private static char option;

	private static int lengthFormat = 40;

	public static void Menu() throws UnknownHostException, IOException, InterruptedException {
		System.out.println(Utils.repeat(lengthFormat, "="));
		System.out.println(Utils.center("Quem eu sou?", lengthFormat));
		System.out.println(Utils.repeat(lengthFormat, "="));
		System.out.println(Utils.center("(C)riar sessão", lengthFormat));
		System.out.println(Utils.center("(E)ntrar em uma sessão", lengthFormat));
		System.out.println(Utils.center("(I)nstruções", lengthFormat));
		System.out.println(Utils.center("(S)air", lengthFormat));
		System.out.println(Utils.repeat(lengthFormat, "="));
		System.out.print(inputForm);
		
		option = reader.nextLine().charAt(0);
		System.out.println(Utils.repeat(lengthFormat, "="));
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
			System.out.println("Por favor, digite uma das opções acima!");
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

		System.out.println("Você foi conectado na porta (" + String.valueOf(port) + ")\n");

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
				System.out.println("Tentando conectar...");
			}
		}
	}

	public static void getMaxPlayers() {
		System.out.println("Me diga a quantidade de player para esta sessão. Intervalo [2, 10].");
		System.out.print(inputForm);
		maxPlayers = reader.nextInt();
		gameManager.setMaxPlayers(maxPlayers);

		while (maxPlayers <= 1 || maxPlayers > 10) {
			System.out.println("ERRO: Por favor, digite um valor entre 2 e 10.");
			System.out.print(inputForm);
			maxPlayers = reader.nextInt();
			gameManager.setMaxPlayers(maxPlayers);
		}
	}

	public static void SignInSession() throws UnknownHostException, IOException {
		System.out.println("Para se conectar a uma sessão você deve fornecer a porta da sessão dos seus amigos.");
		System.out.print("Porta: ");
		port = reader.nextInt();

		System.out.println("Antes de prosseguir, por qual nome você quer ser chamado?");
		System.out.print(inputForm);
		String nickname = reader.next();

		Socket socket = new Socket(host, port);

		Player player = Utils.createPlayer(nickname, socket);
		player.getMessager().sendMessage(nickname);
		String response = player.getMessager().receiveMessage();

		while (!response.equals("ok")) {
			System.out.println("ERRO: Este nome já existe, me diga outro.");
			System.out.print(inputForm);
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
