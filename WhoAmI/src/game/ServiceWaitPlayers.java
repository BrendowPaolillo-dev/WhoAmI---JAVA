package game;

import java.io.IOException;

public class ServiceWaitPlayers implements Runnable {
	
	GameManager gameManager;
	
	public ServiceWaitPlayers(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	@Override
	public void run() {
		try {
			gameManager.waitForPlayer();
		} catch (IOException e) {
			// TODO: What to do here?
			e.printStackTrace();
		}
	}
	
}
