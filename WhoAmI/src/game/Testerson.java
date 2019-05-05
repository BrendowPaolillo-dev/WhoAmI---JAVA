package game;

import java.io.IOException;
import java.util.Arrays;

public class Testerson extends Scorable {

	private static HighScore hs = new HighScore();
	
	Testerson(String nickname, int score) {
		this.nickname = nickname;
		this.score = score;
	}
	
	private void testSave() {
		System.out.println("Begin save");
		HighScore hs = new HighScore();
		
		hs.add(new Testerson("b", 1));
		hs.add(new Testerson("c", 2));
		try {
			hs.save();
		} catch (IOException e) {
			System.out.println("Um erro aconteceu ao tentar salvar no arquivo");
			e.printStackTrace();
		}
		System.out.println(hs);
		System.out.println("End save");
	}
	
	private void testLoad() throws IOException {
		System.out.println("Begin load");
		hs.load();
		System.out.println(hs);
		System.out.println("End load");
	}
	
	private void testDistinct() {
		Arrays.asList(hs.toString().split(",")).stream().distinct().forEach(score -> {
			System.out.println(score);
		});
	}
	
	public static void main(String[] args) throws IOException {

	}
}