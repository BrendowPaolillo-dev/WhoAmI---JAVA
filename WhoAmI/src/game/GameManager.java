package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GameManager implements Runnable {

	private ServerSocket serverSocket;
	private ArrayList<Player> players = new ArrayList<>();

	private int turn = 0;
	private int round = 0;
	private int numberOfTurns = 10;
	private int numberOfRounds = 10;
	
	// TODO: The server can do some restrictions about the content of user responses
	private String responseRequest;
	
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

		System.out.println("DEBUG: O jogador " + nickname + " foi conectado.");
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
		this.responseRequest = this.receiveMessage(player);
	}
	// Making a request for a player and propagate the answer.
	public void requestPlayer(Player player, String request) {
		requestPlayerSilent(player, request);
		this.broadcastPlayerMessage(player, this.responseRequest);
	}

	// Game logic
	public void turnGame(Player master) {
		this.turn = 0;
		while (this.turn < this.numberOfTurns) {
			for (Player player : players) {
				if (master == player) // O jogador mestre dele ser diferente do jogador que vai perguntar
					continue;

				this.broadcast("print.Turno " + String.valueOf(this.turn + 1) + " : Jogador " + player.getNickname());
				this.requestPlayer(player, "question.");
				this.requestPlayer(master, "answer.");
				this.requestPlayer(player, "attempt.");
				this.requestPlayer(master, "answer.");

				this.turn++; // Cada tentativa conta como um turno
				if (this.turn < this.numberOfTurns)
					break;
			}
		}
	}
	public void roundGame() {
		for (Player master : players) {
			this.broadcast("print.Turno " + String.valueOf(this.round + 1) + " : Mestre " + master.getNickname());
			this.requestPlayerSilent(master, "persona.");
			this.requestPlayer(master, "tip.");
			this.turnGame(master);

			this.round++; // Cada personagem conta uma rodada
			if (this.round < this.numberOfRounds)
				break;

		}
	}
	public void run() {
		this.round = 0;
		while (this.round < this.numberOfRounds) {
			this.roundGame();
//			broadcast("print.Ola");
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	// Force method to not need call the waitForPlayers
	public void addPlayer(Player player) {
		players.add(player);
	}
}
