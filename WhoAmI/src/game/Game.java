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

	private boolean validAnswer(String response) {
		if (response.isEmpty())
			return false;
		switch (response.charAt(0)) {
		case 's':
		case 'S':
		case 'n':
		case 'N':
			return true;
		}
		return false;
	}
	
	public void run() throws IOException, InterruptedException {
		while (true) {
			System.out.println("");
			instruction = player.getMessager().receiveMessage();
			
			
			if (instruction.contains(player.getNickname() + ".")) {
				if (instruction.contains("answer.")) {

					// Resposta a pergunta feita nesta rodada
					System.out.print("[Mestre] Digite (S)im ou (N)ao: ");
					response = reader.nextLine();
					while (!this.validAnswer(response)) {
						System.out.print("[Mestre] Por favor, digite (S)im ou (N)ao: ");
						response = reader.nextLine();
					}
					player.getMessager().sendMessage(response);

				} else if (instruction.contains("question.")) {
					// Pergunta para o personagem da rodada
					System.out.print("[Jogador] Faça uma pergunta: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);

				} else if (instruction.contains("attempt.")) {

					// Tentativa de acertar o nome
					System.out.print("[Jogador] Quem você acha que esse personagem é: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);

				} else if (instruction.contains("persona.")) {

					// Tentativa de acertar o nome
					System.out.print("[Mestre] Qual personagem você quer ser: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);

				} else if (instruction.contains("tip.")) {

					// Tentativa de acertar o nome
					System.out.print("[Mestre] Dica: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);

				}
			} else if (instruction.contains("print.")) {

				instruction = instruction.replaceAll("print.", "");
				if (instruction.contains("tip.")) {
					System.out.println("A dica foi: " + instruction.replaceAll("tip.", ""));
				} else if (instruction.contains("answer.")) {
					if (instruction.contains("attempt.")) {
						instruction = instruction.replaceAll("answer.attempt.", "");
						if (instruction.toLowerCase().charAt(0) == 's') {
							System.out.println("[ACERTO] Resposta " + instruction);
						} else {
							System.out.println("[ ERRO ] Resposta " + instruction);
						}
					} else if (instruction.contains("question.")) {
						instruction = instruction.replaceAll("answer.question.", "");
						if (instruction.toLowerCase().charAt(0) == 's') {
							System.out.println("[Mestre] Resposta foi SIM");
						} else {
							System.out.println("[Mestre] Resposta foi NÃO");
						}
					}
				} else if (instruction.contains("question.")) {
					System.out.println("A pergunta foi: " + instruction.replaceAll("question.", ""));
				} else if (instruction.contains("attempt.")) {
					System.out.println("A tentativa foi: " + instruction.replaceAll("attempt.", ""));
				} else {
					System.out.println(instruction);
				}

			} else if (instruction.contains("bar.")) {
				System.out.println("==========================================");
			} else {
				System.out.println("DEBUG: Instrução não especificada!");
				System.out.println("DEBUG: " + instruction);
			}
		}
	}
}
