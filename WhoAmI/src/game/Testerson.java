package game;

import java.io.IOException;

public class Testerson extends Scorable {
	
	Testerson(String nickname, int score) {
		this.nickname = nickname;
		this.score = score;
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Begin save");
		HighScore hs = new HighScore();
		hs.add(new Testerson("a", 0));
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
		System.out.println("Begin load");
		hs.load();
		System.out.println(hs);
		System.out.println("End load");
		
	}
}