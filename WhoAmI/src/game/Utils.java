package game;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Utils {

	private static int lengthFormat = 70;
	private static String inputForm = ">> ";
	private static Scanner reader = new Scanner(System.in);

	public static Player createPlayer(String nickname, Socket socket) throws IOException {
		Messager m = new Messager(socket);
		Player player = new Player(nickname, m);
		return player;
	}

	public static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}

	public static String center(String text, int len) {
		String out = String.format("%" + len + "s%s%" + len + "s", "", text, "");
		float mid = (out.length() / 2);
		float start = mid - (len / 2);
		float end = start + len;
		return out.substring((int) start, (int) end);
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

	public static String getString() {
		String value = new String();
		while (value.isEmpty())
			value = reader.nextLine();
		return value;
	}

	public static String getAnyString() {
		return reader.nextLine();
	}

	public static Boolean isValidIP(String IP) {
		if(IP.equals("localhost"))
			return true;
		
		String[] parts = IP.split("\\.");
		if (parts.length != 4)
			return false;

		for (int i = 0; i < 4; i++) {
			if (!isInteger(parts[i])) {
				return false;
			} else {
				int octeto = Integer.parseInt(parts[i]);
				if (octeto < 0 || octeto > 255)
					return false;
			}
		}
		return true;
	}
	
	public static Boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	public static Boolean isInteger(String str) {
		return str.matches("-?\\d+");
	}

	public static Boolean isSimNao(String str) {
		return str.matches("(?i)s(im)?|n([aã]o)?");
	}
	
	public static int getInt() {
		String value = new String();
		while (!isInteger(value))
			value = reader.nextLine();
		return Integer.parseInt(value);
	}
}
