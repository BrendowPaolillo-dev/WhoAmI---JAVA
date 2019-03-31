package game;

import java.io.IOException;
import java.net.Socket;

public class Utils {
	public static Player createPlayer(String nickname, Socket socket) throws IOException {
		Messager m = new Messager(socket);
		Player player = new Player(nickname, m);
		return player;
	}
}
