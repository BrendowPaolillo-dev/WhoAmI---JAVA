package game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HighScore {
	private List<Scorable> highScore;

	public static String FILENAME = "highscore.txt";
	private File file;

	public HighScore() {
		Path currentRelativePath = Paths.get("");
		String directory = currentRelativePath.toAbsolutePath().toString();
		String absolutePath = directory + File.separator + HighScore.FILENAME;
		this.file = new File(absolutePath);
		this.highScore = new ArrayList<>();
	}

	public void add(Scorable scorable) {
		this.highScore.add(scorable);
	}

	public void addAll(List<Scorable> scorables) {
		this.highScore.addAll(scorables);
	}

	private void sort() {
		Collections.sort(this.highScore, Collections.reverseOrder());
	}

	public String toString() {
		return this.highScore.stream().map(score -> {
			return String.join(" ", score.getNickname(), String.valueOf(score.getScore()));
		}).collect(Collectors.joining(","));
	}

	public void save() throws IOException {
		// Overwrite the data of the file
		this.sort();
		FileWriter fileWriter = new FileWriter(this.file.getPath());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		bufferedWriter.write(this.toString().replaceAll(",", "\n"));
		
		bufferedWriter.close();
	}
}
