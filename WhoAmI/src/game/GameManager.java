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

	public GameManager(ServerSocket serverSocket) throws UnknownHostException, IOException {
		this.serverSocket = serverSocket;
	}

	private boolean validAnswer(String response) {
		if (response.isEmpty())
			return false;
		switch (response.charAt(0)) {
		case 's':
		case 'S':
		case 'n':
		case 'N':
			return true;
		}
		return false;
	}

	// Receive from one player
	private String receiveMessage(Player player) {
		try {
			return player.getMessager().receiveMessage();
		} catch (IOException e) {
			// Maybe, he disconnect ...
			// TODO: Remove the player and tell to another players
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
			e.printStackTrace();
			// Maybe, he disconnect ...
			// TODO: Remove the player and tell to another players
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
		this.broadcast("print.> Somente são permitidas perguntas com respostas do tipo SIM/NAO\n"
				+ "print.> Perguntas inadequadas serão invalidadas pelo Mestre\n"
				+ "print.> Jogador perde a vez se fizer pergunta inadequada");
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
	private void requestPlayerSilent(Player player, String request) {
		String message = player.getNickname() + "." + request;
		this.sendMessage(player, message);
		this.responseRequest = request + this.receiveMessage(player);
	}

	// Making a request for a player and propagate the answer.
	private void requestPlayer(Player player, String request, String response) {
		requestPlayerSilent(player, request);
		this.responseRequest = this.responseRequest.replaceFirst(request, ""); // Remove the type of requisition
		this.broadcastPlayerMessage(player, "println." + response + this.responseRequest);
	}

	// Game logic
	public void run() {
		this.waitPlayers();
		this.showInstructions();

		this.round = 0;
		while (this.round < this.numberOfRounds) {
			this.roundGame();
		}

		// TODO: Broadcast the highscore and create a method to each player save them

		// Simple test
		Collections.sort(this.players, Collections.reverseOrder());
		this.broadcast("printr.=");
		this.broadcast("printc.O jogador " + this.players.get(0).getNickname() + " ganhou!");
		this.players.forEach(player -> {
			this.broadcast("print." + player.getNickname() + ": " + String.valueOf(player.getScore()));
		});
	}

	private void roundGame() {
		for (Player master : players) {
			this.scoreTurn = scoreTurnDefault;

			String round = String.valueOf(this.round + 1);
			String maxRound = String.valueOf(this.numberOfRounds);
			this.broadcast("printr.=");
			this.broadcast("printc.[Round " + round + "-" + maxRound + "] : Mestre (" + master.getNickname() + ")");

			this.sendMessage(master, "print.[Mestre] Qual personagem você quer ser: ");
			this.requestPlayerSilent(master, "request.");

			this.personaCheck = this.responseRequest.replaceFirst("request.", "");

			this.sendMessage(master, "print.[Mestre] Dica: ");
			this.requestPlayer(master, "request.", "Dica: ");

			this.turnGame(master);

			this.round++; // Cada personagem conta uma rodada

			if (this.round > this.numberOfRounds)
				break;

		}
	}

	private Boolean endedTurn() {
		return this.turn < this.numberOfTurns;
	}

	private void turnGame(Player master) {
		this.turn = 0;
		String turns = String.valueOf(this.numberOfTurns + 1);
		while (this.endedTurn() || this.scoreTurn <= 0) {
			for (Player player : players) {
				if (master == player) // O jogador mestre deve ser diferente do jogador que vai perguntar
					continue;

				String turn = "[Turno " + String.valueOf(this.turn + 1) + "-" + turns + "]";
				this.broadcast("printr.=");
				this.broadcast("printc." + turn + " jogador (" + player.getNickname() + ")");

				this.questionRequest(player);
				this.answerRequest(master);
				this.attemptRequest(player);

				if (this.personaCheck.equalsIgnoreCase(this.responseRequest)) {
					this.broadcast("printc.O jogador " + player.getNickname() + " venceu a rodada");
					player.setScore(this.scoreTurn);
					this.endTurns();

				} else {
					this.answerRequest(master);
					if (this.responseRequest.replaceFirst("request.", "").toLowerCase().charAt(0) == 's') {
						this.broadcast("printc.O jogador " + player.getNickname() + " ganhou a rodada");
						player.setScore(this.scoreTurn);
						this.endTurns();
					}
				}
			}
			this.turn++; // Cada tentativa de todos os jogadores conta como um turno
			this.scoreTurn--;
		}
	}

	private void questionRequest(Player player) {
		this.sendMessage(player, "print.[Jogador] Faça uma pergunta");
		this.requestPlayer(player, "request.", "A pergunta foi: ");
	}

	private void answerRequest(Player master) {
		this.sendMessage(master, "print.[Mestre] Digite (S)im ou (N)ao");
		this.requestPlayer(master, "request.", "A resposta foi: ");

		while (!this.validAnswer(this.responseRequest)) {
			this.sendMessage(master, "print.Por favor, digite (S)im ou (N)ao");
			this.requestPlayer(master, "request.", "A resposta foi: ");
		}
	}

	private void attemptRequest(Player player) {
		this.sendMessage(player, "print.[Jogador] Quem você acha que esse personagem é");
		this.requestPlayer(player, "request.", "A tentativa foi: ");
	}

	public void endTurns() {
		this.turn = this.numberOfTurns + 1;
	}

	// Force method to not need call the waitForPlayers
	public void addPlayer(Player player) {
		players.add(player);
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public List<Player> getPlayers() {
		return players;
	}

}
