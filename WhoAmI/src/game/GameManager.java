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

	private ServerSocket serverSocket;
	private ArrayList<Player> players = new ArrayList<>();
	private int maxPlayers = 10;
	private int numberOfPlayers = 1;

	private int turn = 0;
	private int round = 0;
	private int numberOfTurns = 2;
	private int numberOfRounds = 2;
	private int scoreTurn;

	// TODO: The server can do some restrictions about the content of user responses
	private String responseRequest;
	// get the persona of the match for check
	private String personaCheck;

	public GameManager(ServerSocket serverSocket) throws UnknownHostException, IOException {
		this.serverSocket = serverSocket;
	}

	// Receive from one player
	public String receiveMessage(Player player) {
		try {
			return player.getMessager().receiveMessage();
		} catch (IOException e) {
			// TODO: Maybe the player disconnect
			// then, i need remove it
			e.printStackTrace();
		}
		return "";
	}

	// Send to one player
	public void sendMessage(Player player, String message) {
		try {
			player.getMessager().sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
			// Maybe, he disconnect ...
			// TODO: Remove the player and tell to another players
			this.broadcastPlayerMessage(player, "print.Jogador " + player.getNickname() + " foi desconectado!");
			players.remove(player);
		}
	}

	// Send to all players
	public void broadcast(String message) {
		players.forEach(player -> {
			try {
				player.getMessager().sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	// Send to all players, except the player if the nickname
	public void broadcastPlayerMessage(Player player, String message) {
		players.forEach(p -> {
			if (p != player) {
				try {
					p.getMessager().sendMessage("print." + message);
				} catch (IOException e) {
					// TODO: maybe he disconnect
					e.printStackTrace();
				}
			}
		});
	}

	// Listen for players (sockets connection)
	public void waitForPlayer() throws IOException {
		Socket socket = this.serverSocket.accept();
		Messager messager = new Messager(socket);

		String nickname = messager.receiveMessage();

		Player player = this.getPlayer(nickname);
		while (player != null) {
			messager.sendMessage("not ok");
			nickname = messager.receiveMessage();
			player = this.getPlayer(nickname);
		}
		messager.sendMessage("ok");

		Player newPlayer = new Player(nickname, messager);
		players.add(newPlayer);
		this.numberOfPlayers++;
		String interval = String.valueOf(this.numberOfPlayers) + "/" + String.valueOf(this.maxPlayers);
		this.broadcast("print.[" + interval + "] O jogador (" + nickname + ") foi conectado.");
	}

	public void waitPlayers() throws IOException, InterruptedException {
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 1; i < this.maxPlayers; i++) {
			es.execute(new ServiceWaitPlayers(this));
		}
		es.shutdown();
		boolean finished = es.awaitTermination(120, TimeUnit.SECONDS);
		// all tasks have finished or the time has been reached.
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
	public void requestPlayerSilent(Player player, String request) {
		String message = player.getNickname() + "." + request;
		this.sendMessage(player, message);
		this.responseRequest = request + this.receiveMessage(player);
	}

	// Making a request for a player and propagate the answer.
	public void requestPlayer(Player player, String request) {
		requestPlayerSilent(player, request);
		this.broadcastPlayerMessage(player, this.responseRequest);
	}

	// Game logic
	public void turnGame(Player master) {
		this.turn = 0;
		while (this.turn < this.numberOfTurns || this.scoreTurn <= 0) {
			for (Player player : players) {
				if (master == player) // O jogador mestre deve ser diferente do jogador que vai perguntar
					continue;
				this.broadcast("print.[Turno " + String.valueOf(this.turn + 1) + "] jogador: " + player.getNickname());
				this.requestPlayer(player, "question.");
				this.requestPlayer(master, "answer.question.");
				this.requestPlayer(player, "attempt.");

				if (this.personaCheck.toLowerCase()
						.equals(this.responseRequest.replaceFirst("attempt.", "").toLowerCase())) {
					this.broadcast("print.O jogador " + player.getNickname() + " venceu a rodada");
					this.turn = this.numberOfTurns + 1;
					player.setScore(this.scoreTurn);

				} else {
					this.requestPlayer(master, "answer.attempt.");
					if (this.responseRequest.replaceFirst("answer.attempt.", "").toLowerCase().charAt(0) == 's') {
						this.broadcast("print.O jogador " + player.getNickname() + " ganhou a rodada");
						this.turn = this.numberOfTurns + 1;
						player.setScore(this.scoreTurn);
					}
				}
			}
			this.turn++;// Cada tentativa de todos os jogadores conta como um turno
			this.scoreTurn--;
		}
	}

	public void roundGame() {
		for (Player master : players) {
			this.scoreTurn = 10;
			
			this.broadcast("print.Turno " + String.valueOf(this.round + 1) + " : Mestre " + master.getNickname());
			
			this.requestPlayerSilent(master, "persona.");
			this.personaCheck = this.responseRequest.replaceFirst("persona.", "");
			
			this.requestPlayer(master, "tip.");

			this.turnGame(master);

			this.round++; // Cada personagem conta uma rodada

			if (this.round > this.numberOfRounds)
				break;

		}
	}

	public void run() {
		try {
			this.waitPlayers();
		} catch (IOException | InterruptedException e) {
			// TODO: What do here
			e.printStackTrace();
		}

		this.round = 0;
		while (this.round < this.numberOfRounds) {
			this.roundGame();
		}
		Collections.sort(this.players, Collections.reverseOrder());
		this.players.forEach(player -> {
			this.broadcast("print." + player.getNickname() + ": " + String.valueOf(player.getScore()));
		});

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
}
