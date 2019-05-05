package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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

	public String toString() {
		return this.highScore.stream().map(score -> score.toString()).collect(Collectors.joining(","));
	}

	public void clear() throws IOException {
		FileWriter fileWriter = new FileWriter(this.file.getPath());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write("");
		bufferedWriter.close();
	}
	
	public void save() throws IOException {
		// Overwrite the data of the file
		Collections.sort(this.highScore, Collections.reverseOrder());
		FileWriter fileWriter = new FileWriter(this.file.getPath());
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		bufferedWriter.write(this.toString().replaceAll(",", "\n"));
		
		bufferedWriter.close();
	}
	
	public void load() throws IOException {
		this.highScore.clear();
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] parts = line.split(" ");
			if (parts.length > 1) {
				String name = parts[0];
				int value = Integer.parseInt(parts[1]);
				this.highScore.add(new Score(name, value));
			}
		}
		
		bufferedReader.close();
	}
}
