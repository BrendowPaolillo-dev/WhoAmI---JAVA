package game;

public abstract class Scorable implements Comparable<Scorable> {
	protected String nickname;
	protected int score;

	@Override
	public int compareTo(Scorable other) {
		return this.getScore() - other.getScore();
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
		this.score = score;
	}

	public String toString() {
		return String.join(" ", this.getNickname(), String.valueOf(this.getScore()));
	}
}
