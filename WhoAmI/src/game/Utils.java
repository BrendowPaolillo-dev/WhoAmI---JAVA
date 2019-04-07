package game;

import java.io.IOException;
import java.net.Socket;

public class Utils {
	public static Player createPlayer(String nickname, Socket socket) throws IOException {
		Messager m = new Messager(socket);
		Player player = new Player(nickname, m);
		return player;
	}
	
	public static String cls() {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < 20; i++ )
			sb.append('\n');
		return sb.toString();
	}
}
