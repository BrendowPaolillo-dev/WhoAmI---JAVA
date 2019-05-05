package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
	// General use
	private static GameManager gameManager;
	private static Game game;
	private static HighScore highScore = new HighScore();

	// Variables to connection
	private static String host = "localhost";
	private static int port = 12112;
	private static int maxPlayers = 0;

	private static String option;
	private static int titleTheme = 3;

	private static void title1() {
		Utils.print("   ___                            ");
		Utils.print("  / _ \\ _   _  ___ _ __ ___       ");
		Utils.print(" | | | | | | |/ _ \\ '_ ` _ \\      ");
		Utils.print(" | |_| | |_| |  __/ | | | | |     ");
		Utils.print("  \\__\\_\\\\__,_|\\___|_| |_| |_| ___ ");
		Utils.print("  ___  ___  _   _    ___ _   |__ \\");
		Utils.print(" / __|/ _ \\| | | |  / _ \\ | | |/ /");
		Utils.print(" \\__ \\ (_) | |_| | |  __/ |_| |_| ");
		Utils.print(" |___/\\___/ \\__,_|  \\___|\\__,_(_) ");
	}

	private static void title2() {
		Utils.print("    ____                             ");
		Utils.print("   / __ \\                            ");
		Utils.print("  | |  | |_   _  ___ _ __ ___        ");
		Utils.print("  | |  | | | | |/ _ \\ '_ ` _ \\  ___  ");
		Utils.print("  | |__| | |_| |  __/ | | | | ||__ \\ ");
		Utils.print(" __\\___\\_\\\\__,_|\\___|_| |_| |_|   ) |");
		Utils.print("/ __|/ _ \\| | | |  / _ \\ | | |   / / ");
		Utils.print("\\__ \\ (_) | |_| | |  __/ |_| |  |_|  ");
		Utils.print("|___/\\___/ \\__,_|  \\___|\\__,_|  (_)  ");
		System.out.println();
	}

	private static void title3() {
		Utils.print("        ___   __ __  ____ ___  ___        ");
		Utils.print("       // \\\\  || || ||    ||\\\\//||        ");
		Utils.print("      ((   )) || || ||==  || \\/ ||        ");
		Utils.print("       \\\\_/X| \\\\_// ||___ ||    ||        ");
		Utils.print(" __    ___   __ __     ____ __ __    ____ ");
		Utils.print("(( \\  // \\\\  || ||    ||    || ||    |  \\\\");
		Utils.print(" \\\\  ((   )) || ||    ||==  || ||      _//");
		Utils.print("\\_))  \\\\_//  \\\\_//    ||___ \\\\_//      || ");
		Utils.print("                                          ");
	}

	private static void menu() throws UnknownHostException, IOException, InterruptedException {
		String options = "ceispoCEISPO";
		do {

			Utils.printr("=");
			switch (titleTheme) {
			case 1:
				title1();
				break;
			case 2:
				title2();
				break;
			case 3:
				title3();
				break;
			default:
				System.out.println("[ERRO] Tem alguma coisa errada com a seleção de temas!!!");
				break;
			}
			Utils.printr("=");
			Utils.print("(C)riar sessão");
			Utils.print("(E)ntrar em uma sessão");
			Utils.print("(O)pções de jogo");
			Utils.print("(I)nformações");
			Utils.print("(P)ontuações");
			Utils.print("(S)air");
			System.out.println();
			Utils.printIn();

			option = Utils.getString();
			option = String.valueOf(option.charAt(0));

			Utils.printr("=");
			switch (option.charAt(0)) {
			case 'c':
			case 'C':
				createSession();
				break;
			case 'e':
			case 'E':
				signInSession();
				break;
			case 'p':
			case 'P':
				showHighScore();
				break;
			case 'o':
			case 'O':
				settings();
				break;
			case 'i':
			case 'I':
				informations();
				break;
			case 's':
			case 'S':
				break;
			default:
				Utils.print("Por favor, digite uma das opções acima!");
				break;
			}
		} while (!options.contains(option));
	}

	private static void settings() {
		do {
			System.out.println();
			Utils.print("Opções de configuração");
			System.out.println();
			Utils.print("(1) Alterar IP - (" + host + ")");
			Utils.print("(2) Alterar título - [1,2,3] Tema " + String.valueOf(titleTheme));
			Utils.print("(3) Apagar histórico de pontuações.");
			Utils.print("(4) Concluir");

			System.out.println();
			Utils.printIn();

			option = Utils.getString();
			option = String.valueOf(option.charAt(0));

			Utils.printr("=");
			switch (option.charAt(0)) {
			case '1':
				// TODO: Get the IP and verify if he is valid - make a mask, regular expression
				break;
			case '2':
				// TODO: Get the theme
				break;
			case '3':
				Utils.print("Você realmente quer apagar o seu histórico?");
				System.out.println();
				Utils.printIn();
				option = Utils.getString();
				if (option.charAt(0) == 's' || option.charAt(0) == 'S') {
					try {
						highScore.clear();
					} catch (IOException e) {
						Utils.print("Erro ao tentar limpar o arquivo.");
						e.printStackTrace();
						return;
					}
				}
				break;
			case '4':
				break;
			default:
				Utils.print("Por favor, digite uma das opções acima!");
				break;
			}
		} while (!(option.charAt(0) == '4'));
	}

	private static void showHighScore() {
		try {
			highScore.load();
		} catch (IOException e) {
			Utils.print("Erro ao ler o highscore");
			e.printStackTrace();
			return;
		}

		String[] arrayHighScore = highScore.toString().split(",");
		if (arrayHighScore.length == 1 && arrayHighScore[0].isEmpty()) {
			Utils.print("Não há nenhuma pontuação feita ainda...");
			Utils.print("Volte aqui depois de participar de uma partida!");
		} else {
			Utils.print("Jogador < - > Pontuação");
			for (int i = 0; i < arrayHighScore.length; i++) {
				Utils.print(arrayHighScore[i].replaceFirst(" ", " < - > "));
			}
		}
		System.out.println();
		Utils.printr("=");
		Utils.print("Precione qualquer tecla para continuar...");
		Utils.printr("=");
		Utils.getAnyString();
	}

	private static void createSession() throws IOException, InterruptedException {
		Utils.print("Antes de prosseguir, por qual nome você quer ser chamado?");
		System.out.println();
		Utils.printIn();

		String nickname = Utils.getString();
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

	private static ServerSocket tryConnection() throws InterruptedException {
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

	private static void getMaxPlayers() {
		Utils.print("Me diga a quantidade de player para esta sessão.");
		Utils.print("Intervalo [2, 10].");
		System.out.println();
		Utils.printIn();

		maxPlayers = Utils.getInt();
		gameManager.setMaxPlayers(maxPlayers);

		while (maxPlayers <= 1 || maxPlayers > 10) {
			Utils.print("ERRO: Por favor, digite um valor entre 2 e 10.");
			System.out.println();
			Utils.printIn();

			maxPlayers = Utils.getInt();
			gameManager.setMaxPlayers(maxPlayers);
		}

		Utils.print("Esperando pelos jogadores...");
	}

	private static void signInSession() throws UnknownHostException, IOException {
		Utils.print("Para se conectar a uma sessão você deve fornecer a");
		Utils.print("(porta) da sessão dos seus amigos.");
		System.out.println();
		Utils.printIn();

		port = Utils.getInt();

		System.out.println();
		Utils.print("Antes de prosseguir, por qual nome você quer ser chamado?");
		System.out.println();
		Utils.printIn();

		String nickname = Utils.getString();
		Socket socket = new Socket(host, port);
		Player player = Utils.createPlayer(nickname, socket);
		player.getMessager().sendMessage(nickname);
		String response = player.getMessager().receiveMessage();

		while (!response.equals("ok")) {
			Utils.print("ERRO: Este nome já existe, me diga outro.");
			System.out.println();
			Utils.printIn();
			nickname = Utils.getString();
			player.setNickname(nickname);
			player.getMessager().sendMessage(nickname);
			response = player.getMessager().receiveMessage();
		}
		game = new Game(player);
	}

	private static void informations() {
		// TODO: Write instruction prints
		Utils.printr("=");
		System.out.println();
		Utils.print("Este jogo é a reformulação do jogo \"Who am I\"");
		Utils.print("Para iniciar o jogo, é selecionado um mestre,");
		Utils.print("no qual definirá o seu personagem para ser adivinhado");
		Utils.print("pelos outros jogadores.");
		Utils.print("Os outros jogadores, por sua vez, precisam acertar o personagem,");
		Utils.print("fazendo apenas perguntas que apenas possam ser respondidas com");
		Utils.print("\"sim\" ou \"não\".");
		Utils.print("Os mestres são redefinidos assim que todos acertarem o personagem");
		Utils.print("ou se o limite de rodadas for atingido.");
		Utils.print("Os pontos são atrubuídos de maneira descrescente, ou seja");
		Utils.print("A primeira rodada de tentativas vale 10 pontos,");
		Utils.print("a segunda, 9 pontos, e assim em diante.");
		Utils.print("Ganha quem fizer mais pontos ao final da partida.");	
		System.out.println();
		Utils.print("Este jogo foi feito como um projeto para");
		Utils.print("a matéria de Linguagens de Programação.");
		System.out.println();
		Utils.print("Desenvolvedores");
		Utils.print("Daniel Augusto Rodrigues Farina");
		Utils.print("Brendow Paolillo Castro Isidoro");
		System.out.println();
		Utils.print("Bom jogo!");
		System.out.println();
		Utils.printr("=");
		Utils.print("Pressione qualquer tecla para continuar...");
		Utils.printr("=");
		Utils.getAnyString();
	}

	private static void run() throws IOException, InterruptedException {
		String runnable = "ceCE";
		if (!runnable.contains(option))
			return;

		if (gameManager != null) {
			Thread thread = new Thread(gameManager);
			thread.start();
		}
		game.run();
		gameManager.getPlayers().forEach(player -> highScore.add(player));
	}

	private static void exitMessage() {
		Utils.print("  *´¨)                          ");
		Utils.print(" ¸.·´¸.·*´¨) ¸.·*¨)             ");
		Utils.print("(¸.·´ (¸.·` ** Até a próxima! **");
		Utils.printr("=");
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String exit = "sS";
		do {
			menu();
			run();
		} while (!exit.contains(option));
		exitMessage();
	}

}
