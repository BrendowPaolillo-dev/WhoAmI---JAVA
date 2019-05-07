package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameManager implements Runnable {
	private final int scoreTurnDefault = 10;

	private ServerSocket serverSocket;
	private List<Player> players = new ArrayList<>();
	private int maxPlayers = 10;
	private int numberOfPlayers = 1;

	private int turn = 0;
	private int round = 0;
	private int numberOfTurns = 2;
	private int numberOfRounds = 2;
	private int scoreTurn = scoreTurnDefault;

	// TODO: The server can do some restrictions about the content of user responses
	private String responseRequest;
	// get the persona of the match for check
	private String personaCheck;

	private String attemptPersona;
	private List<Player> winners = new ArrayList<>();

	public GameManager(ServerSocket serverSocket) throws UnknownHostException, IOException {
		this.serverSocket = serverSocket;
	}

	private boolean validAnswer(String response) {
		return response.matches("(?i)s(im)?|n([aã]o)?");
	}

	// Receive from one player
	private String receiveMessage(Player player) {
		try {
			return player.getMessager().receiveMessage();
		} catch (IOException e) {
			this.broadcastPlayerMessage(player, "printc.Jogador " + player.getNickname() + " foi desconectado!");
			players.remove(player);
		}
		return "";
	}

	// Send to one player
	private void sendMessage(Player player, String message) {
		try {
			player.getMessager().sendMessage(message);
		} catch (IOException e) {
			this.broadcastPlayerMessage(player, "printc.Jogador " + player.getNickname() + " foi desconectado!");
			players.remove(player);
		}
	}

	// Send to all players
	private void broadcast(String message) {
		players.forEach(player -> {
			try {
				player.getMessager().sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	// Send to all players, except to the player 'player'
	private void broadcastPlayerMessage(Player player, String message) {
		players.forEach(p -> {
			if (p != player) {
				try {
					p.getMessager().sendMessage(message);
				} catch (IOException e) {
					// TODO: maybe he disconnect
					e.printStackTrace();
				}
			}
		});
	}

	private void showLobby() {
		this.broadcast("printr.=");
		this.broadcast("printc.Jogadores conectados: " + String.valueOf(this.numberOfPlayers) + "/"
				+ String.valueOf(this.maxPlayers));
		this.players.forEach(player -> {
			this.broadcast("print.\t[" + player.getNickname() + "]");
		});
		this.broadcast("print.");
	}

	private void showInstructions() {
		this.broadcast("printr.=");
		this.broadcast("printc.Instruções básicas:");
		this.broadcast("printc.> Somente são permitidas perguntas com respostas do tipo SIM/NAO <\n"
				+ "printc.> Perguntas inadequadas serão invalidadas pelo Mestre <\n"
				+ "printc.> Jogador perde a vez se fizer pergunta inadequada <");
		this.broadcast("print.");
	}

	// Listen for players (sockets connection)
	protected void waitForPlayer() throws IOException {
		Socket socket = this.serverSocket.accept();
		Messager messager = new Messager(socket);

		String nickname = messager.receiveMessage();

		Player player = this.getPlayer(nickname);
		while (nickname.length() > 20 || player != null) {
			messager.sendMessage("denial"); // Negative
			nickname = messager.receiveMessage();
			player = this.getPlayer(nickname);
		}
		messager.sendMessage("ok");

		Player newPlayer = new Player(nickname, messager);
		players.add(newPlayer);
		this.numberOfPlayers++;
		this.showLobby();

	}

	// Controlled listen player for all players
	public void waitPlayers() {
		try {
			ExecutorService es = Executors.newCachedThreadPool();
			for (int i = 1; i < this.maxPlayers; i++) {
				es.execute(new ServiceWaitPlayers(this));
			}
			es.shutdown();
			es.awaitTermination(3, TimeUnit.MINUTES); // Max time of wait: 3 minutes
			// all tasks have finished or the time has been reached.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Find a player by nickname
	private Player getPlayer(String nickname) {
		List<Player> copy = new ArrayList<>(players);
		copy.removeIf(player -> {
			return !player.getNickname().equals(nickname);
		});
		return copy.isEmpty() ? null : copy.get(0);
	}

	// Making a request for a player only
	private void requestPlayerSilent(Player player) {
		this.sendMessage(player, "request.");
		this.responseRequest = this.receiveMessage(player);
	}

	// Game logic
	public void run() {
		this.waitPlayers();
		this.showInstructions();

		this.round = 0;
		this.numberOfRounds = this.numberOfPlayers;
		this.numberOfTurns = 10;
		while (this.round < this.numberOfRounds) {
			this.roundGame();
		}

		HighScore highScore = new HighScore();
		try {
			highScore.load();

			this.players.forEach(player -> {
				highScore.add(player);
			});

			highScore.save();
		} catch (IOException e) {
			System.out.println("Erro ao carregar o highScore.");
			e.printStackTrace();
		}

		Collections.sort(this.players, Collections.reverseOrder());
		this.broadcast("printr.=");
		this.broadcast("printc.O jogador '" + this.players.get(0).getNickname() + "' ganhou!");
		for (int i = 0; i < this.players.size(); i++) {
			this.broadcast("printc.(" + String.valueOf(i + 1) + ")  "
					+ players.get(i).toString().replaceFirst(" ", "   -   "));
		}
		this.broadcast("exit.");
		this.close();
	}

	private Boolean hasRound() {
		return this.round < this.numberOfRounds;
	}

	private void roundGame() {
		for (Player master : players) {
			this.scoreTurn = scoreTurnDefault;

			String round = String.valueOf(this.round + 1);
			String maxRound = String.valueOf(this.numberOfRounds);
			this.broadcast("printr.=");
			this.broadcast("printc.[Round " + round + "-" + maxRound + "] : Mestre (" + master.getNickname() + ")");

			this.sendMessage(master, "print.[Mestre] Qual personagem você quer ser");
			this.requestPlayerSilent(master);

			this.personaCheck = this.responseRequest.replaceFirst("request.", "");

			this.sendMessage(master, "print.[Mestre] Dica");
			this.requestPlayerSilent(master);
			this.broadcastPlayerMessage(master, "println.[Mestre] Dica: " + this.responseRequest);

			this.turnGame(master);

			this.round++; // Cada personagem conta uma rodada

			if (!this.hasRound())
				break;
		}
	}

	private Boolean hasTurn() {
		return this.turn < this.numberOfTurns;
	}

	private void turnGame(Player master) {
		this.turn = 0;
		String turns = String.valueOf(this.numberOfTurns);
		winners.clear();
		while (this.hasTurn() && this.scoreTurn > 0) {
			for (Player player : players) {
				// Os jogadores devem ser diferente do mestre e dos ganhadores
				if (master == player || winners.contains(player))
					continue;

				String turn = "[Turno " + String.valueOf(this.turn + 1) + "-" + turns + "]";
				this.broadcast("printr.=");
				this.broadcast("printc." + turn + " jogador (" + player.getNickname() + ")");

				this.question(player);
				this.broadcastPlayerMessage(player, "println.[Jogador] Pergunta: " + this.responseRequest);

				this.answer(master);
				this.responseRequest = this.responseRequest.matches("[Ss](im)?") ? "Sim!" : "Não.";
				this.broadcastPlayerMessage(master, "println.[Mestre] Resposta: " + this.responseRequest);

				this.attempt(player);

				if (this.personaCheck.equalsIgnoreCase(this.responseRequest)) {
					this.broadcast("printc. O jogador '" + player.getNickname() + "' acertou!");
					player.setScore(this.scoreTurn);
					winners.add(player);
				} else {
					this.sendMessage(master, "println.[Jogador] Tentativa: " + this.responseRequest);
					this.answer(master);
					if (this.responseRequest.matches("[Ss](im)?")) {
						this.broadcast("printc. O jogador '" + player.getNickname() + "' acertou!");
						player.setScore(this.scoreTurn);
						winners.add(player);
					} else {
						this.sendMessage(player, "println.[Mestre] Resposta: Não.");
						this.players.forEach(others -> {
							if (others != player && others != master)
								this.sendMessage(others, "println.[Jogador]: Tentativa: " + this.attemptPersona);
						});
					}
				}
			}
			this.turn++; // Cada tentativa de todos os jogadores conta como um turno
			this.scoreTurn--;
		}
	}

	private void question(Player player) {
		this.sendMessage(player, "print.[Jogador] Faça uma pergunta");
		this.requestPlayerSilent(player);
	}

	private void answer(Player master) {
		this.sendMessage(master, "print.[Mestre] Digite (S)im ou (N)ão");
		this.requestPlayerSilent(master);

		while (!this.validAnswer(this.responseRequest)) {
			this.sendMessage(master, "print.Por favor, digite (S)im ou (N)ão");
			this.requestPlayerSilent(master);
		}
	}

	private void attempt(Player player) {
		this.sendMessage(player, "print.[Jogador] Quem você acha que é esse personagem?");
		this.requestPlayerSilent(player);
		this.attemptPersona = this.responseRequest;
	}

	public void endTurns() {
		this.turn = this.numberOfTurns + 1;
	}

	// Force method to not need call the waitForPlayers
	public void addPlayer(Player player) {
		this.players.add(player);
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public void close() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
