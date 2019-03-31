package game;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameHost {
	private GameManager gameManager;

	public GameHost(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public Player createPlayer(String nickname, Socket socket) throws UnknownHostException, IOException {
		Messager messager = new Messager(socket);
		Player player = new Player(nickname, messager);
		this.gameManager.addPlayer(player);
		this.gameManager.getHost().addConnection();
		return player;
	}

	public GameManager getGameManager() {
		return this.gameManager;
	}

	private void broadcast(String message) {
		this.gameManager.broadcast(message);
	}

	public void run() throws InterruptedException {
		while (true) {
			Thread.sleep(1000);
			this.broadcast("Tudo certo capitão?");
		}
	}
}
