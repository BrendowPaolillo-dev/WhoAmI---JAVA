package game;

import java.io.IOException;

public class Player extends Scorable {
	private Messager messager;

	public Player(String nickname, Messager messager) throws IOException {
		this.nickname = nickname;
		this.messager = messager;
		this.score = 0;
	}
	
	@Override
	public void setScore(int score) {
		this.score += score;
	}

	public Messager getMessager() {
		return messager;
	}
}
