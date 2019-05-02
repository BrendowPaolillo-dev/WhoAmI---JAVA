package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class HighScoreGlobal {
	private static String fileName = "highscore.txt";
	private static ArrayList<Player> players = new ArrayList<>();
	private static File file;

	public static void create(ArrayList<Player> players) {
		try {
			load();
			update(players);
		} catch (FileNotFoundException e) {
			createFile();
			update(players);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void update(ArrayList<Player> p) {
		try {
			file = new File(fileName);
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.print(""); // Limpa o arquivo

			players.addAll(p);
			Collections.sort(players, Collections.reverseOrder());
			int lenght = players.size() > 20 ? 20 : players.size();
			for (int i = 0; i < lenght; i++) {
				printWriter.write(players.get(i).getNickname() + "\t" + players.get(i).getScore() + "\n");
			}

			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void load() throws NumberFormatException, IOException {
		// Tentando carregar o arquivo
		FileReader fileWriter = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileWriter);
		
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] highLine = line.split("\t");
			if (highLine.length >= 2) {
				Player player = new Player(highLine[0], null);
				player.setScore(Integer.valueOf(highLine[1]));
				players.add(player);
			}
		}

		bufferedReader.close();
	}

	private static void createFile() {
		try {
			file = new File(fileName);
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
