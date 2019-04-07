package game;

import java.io.IOException;

public class Player implements Comparable<Player> {
	private String nickname;
	private int score;
	private Messager messager;

	public Player(String nickname, Messager messager) throws IOException {
		this.nickname = nickname;
		this.messager = messager;
		this.score = 0;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score += score;
	}

	public Messager getMessager() {
		return messager;
	}

	@Override
	public int compareTo(Player player) {
		return this.getScore() - player.getScore();
	}
}
