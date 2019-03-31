package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class GameManager implements Runnable {
	
	private ServerSocket serverSocket;
	private List<Player> players;
	
	public GameManager(ServerSocket serverSocket) throws UnknownHostException, IOException {
		this.serverSocket = serverSocket;
		this.players = new ArrayList<>();
	}

	// Receive from one player
	public String receiveMessage(String nickname) throws IOException {
		Player player = this.getPlayer(nickname);
		return player.getMessager().receiveMessage();
	}
	
	public String receiveMessage(Player player) {
		try {
			return player.getMessager().receiveMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	// Send to one player
	public void sendMessage(String nickname, String message) {
		try {
			Player player = this.getPlayer(nickname);
			player.getMessager().sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
			// Maybe, he disconnect ...
			// TODO: Remove the player and tell to another players
		}
	}
	
	public void sendMessage(Player player, String message) {
		try {
			player.getMessager().sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
			// Maybe, he disconnect ...
			// TODO: Remove the player and tell to another players
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
	public void broadcastPlayerMessage(String nickname, String message) {
		players.forEach(player -> {
			if (!player.getNickname().equals(nickname)) {
				try {
					player.getMessager().sendMessage(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void waitForPlayer() throws IOException {
		// TODO: create a thread to do that
		Socket socket = this.serverSocket.accept();
		
		Messager messager = new Messager(socket);
		
		// Ensuring that players with nicknames do not exist.
		messager.sendMessage("request.nickname");
		String nickname = messager.receiveMessage();
		Player player = this.getPlayer(nickname);
		while (player != null) {
			messager.sendMessage("request.nickname");
			nickname = messager.receiveMessage();
			player = this.getPlayer(nickname);
		}
		messager.sendMessage("confirmed");
		
		Player newPlayer = new Player(nickname, messager);
		players.add(newPlayer);
	}
	
	// Find a player by nickname
	private Player getPlayer(String nickname) {
		List<Player> copy = new ArrayList<>(players);
		copy.removeIf(player -> {
			return !player.getNickname().equals(nickname);
		});
		return copy.isEmpty() ? null : copy.get(0);
	}
	
	
	public void run() {
		while (true) {
			
			// round
			players.forEach(persona -> {
				players.forEach(player -> {
					if (player != persona) { // Se o jogador personagem for diferente do jogador selecionado a fazer as perguntas
						String message, response;
						
						message = player.getNickname() + ".question.";
						this.sendMessage(player, message);
						response = this.receiveMessage(player);						
						
						message = persona.getNickname() + ".answer.";
						this.sendMessage(persona, message);
						response = this.receiveMessage(persona);
						
						message = player.getNickname() + ".attempt.";
						this.sendMessage(player, message);
						response = this.receiveMessage(player);
					}
				});
			});
				
			
		}
	}

	public void addPlayer(Player player) {
		// TODO Auto-generated method stub
		players.add(player);
	}
}
