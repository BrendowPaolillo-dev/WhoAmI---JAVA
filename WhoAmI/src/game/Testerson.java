package game;

import java.io.IOException;
import java.util.Arrays;

public class Testerson extends Scorable {

	private static HighScore hs = new HighScore();
	
	private static void testSave() {
		System.out.println("Begin save");
		HighScore hs = new HighScore();
		
		hs.add(new Score("b", 1));
		hs.add(new Score("c", 2));
		try {
			hs.save();
		} catch (IOException e) {
			System.out.println("Um erro aconteceu ao tentar salvar no arquivo");
			e.printStackTrace();
		}
		System.out.println(hs);
		System.out.println("End save");
	}
	
	private static void testLoad() throws IOException {
		System.out.println("Begin load");
		hs.load();
		System.out.println(hs);
		System.out.println("End load");
	}
	
	private static void testDistinct() {
		Arrays.asList(hs.toString().split(",")).stream().distinct().forEach(score -> {
			System.out.println(score);
		});
	}
	
	public static void testIsValidIP() {
		System.out.println(Utils.isValidIP("localhost"));
		System.out.println(Utils.isValidIP("192.168.0.3"));
		System.out.println(Utils.isValidIP("0.0.0.0"));
		System.out.println(Utils.isValidIP("10.10.10.10"));
		System.out.println(Utils.isValidIP("168.56.10.1"));
		System.out.println(Utils.isValidIP("256.56.10.1"));
		System.out.println(Utils.isValidIP("-1.56.10.1"));
		System.out.println(Utils.isValidIP("0.56.10.1"));
		System.out.println(Utils.isValidIP("255.56.10.1"));
		System.out.println(Utils.isValidIP("168.56.10"));
		System.out.println(Utils.isValidIP("151.15616"));	
		System.out.println(Utils.isValidIP("asdasds"));		
	}
	
	public static void main(String[] args) throws IOException {
		testIsValidIP();
	}
}