package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class GameManager implements Observer {
	private ArrayList<Player> players;
	private Observable host;

	public GameManager(Observable host) {
		this.host = host;
		this.host.addObserver(this);
		this.players = new ArrayList<>();
	}

	public void addPlayer(Player player) {
		players.add(player);
	}

	public Messager waitPlayers() throws IOException {
		return this.getHost().addConnection();
	}
	
	public void broadcast(Messager messager, String message) {
		this.getHost().broadcast(messager, message);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o != null) {
			players.removeIf(player -> {
				Messager messager = (Messager) arg;
				return player.getMessager().equals(messager);
			});
		}
	}
	
	public Host getHost() {
		return (Host) host;
	}
}
