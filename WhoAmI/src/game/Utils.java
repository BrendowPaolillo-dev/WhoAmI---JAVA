package game;

import java.io.IOException;
import java.net.Socket;

public class Utils {

	private static int lengthFormat = 70;
	private static String inputForm = ">> ";
	
	public static Player createPlayer(String nickname, Socket socket) throws IOException {
		Messager m = new Messager(socket);
		Player player = new Player(nickname, m);
		return player;
	}
	
	public static String repeat(int count, String with) {
	    return new String(new char[count]).replace("\0", with);
	}
	
	public static String center(String text, int len){
	    String out = String.format("%"+len+"s%s%"+len+"s", "",text,"");
	    float mid = (out.length()/2);
	    float start = mid - (len/2);
	    float end = start + len; 
	    return out.substring((int)start, (int)end);
	}
	
	public static void print(String text) {
		System.out.println(Utils.center(text, lengthFormat));
	}
	
	public static void printr(String text) {
		System.out.println(Utils.repeat(lengthFormat, text));
	}
	
	public static void printIn() {
		System.out.print(inputForm);
	}
}
