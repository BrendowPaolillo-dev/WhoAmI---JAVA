package game;

import java.io.IOException;
import java.util.Scanner;

public class Game {
	// General use
	private static Scanner reader = new Scanner(System.in);

	protected Player player;

	private String instruction, response;

	public Game(Player player) {
		this.player = player;
	}

	public void run() throws IOException, InterruptedException {
		while (true) {
			System.out.println("");
			instruction = player.getMessager().receiveMessage();

			if (instruction.contains("request.")) {
				Utils.printIn();
				response = Utils.getString();
				player.getMessager().sendMessage(response);
			} else if (instruction.contains("print.")) {
				instruction = instruction.replaceFirst("print.", "");
				System.out.print(instruction);
			} else if (instruction.contains("printc.")) {
				instruction = instruction.replaceFirst("printc.", "");
				Utils.print(instruction);
				System.out.println();
			} else if (instruction.contains("printr.")) {
				instruction = instruction.replaceFirst("printr.", "");
				Utils.printr(instruction);
				System.out.println();
			} else if (instruction.contains("println.")) {
				instruction = instruction.replaceFirst("println.", "");
				System.out.println(instruction);
			} else {
				System.out.println("DEBUG: Instrução não especificada!");
				System.out.println("DEBUG: " + instruction);
			}
		}
	}
}
