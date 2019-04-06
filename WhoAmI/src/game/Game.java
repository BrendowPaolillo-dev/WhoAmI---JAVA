package game;

import java.io.IOException;
import java.util.Scanner;

public class Game {
	// General use
	private static Scanner reader = new Scanner(System.in);

	protected Player player;

	public Game(Player player) {
		this.player = player;
	}

	private boolean validAnswer(String response) {
		if (response.isEmpty())
			return false;
		switch(response.charAt(0)) {
		case 's':
		case 'S':
		case 'n':
		case 'N':
			return true;
		}
		return false;
	}

	public void run() throws IOException, InterruptedException {
		String instruction, response;
		while (true) {
			System.out.println("Esperando por instruções");
			instruction = player.getMessager().receiveMessage();

			if (instruction.contains(player.getNickname() + ".")) {
				if (instruction.contains("question.")) {
					
					// Pergunta para o personagem da rodada
					System.out.print("Faça uma pergunta: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);
					
				} else if (instruction.contains("answer.")) {
					
					// Resposta a pergunta feita nesta rodada
					System.out.print("Digite (S)im ou (N)ao: ");
					response = reader.next();
					while (!this.validAnswer(response)) {
						System.out.print("Por favor, digite (S)im ou (N)ao: ");
						response = reader.next();						
					}
					player.getMessager().sendMessage(response);
					
				} else if (instruction.contains("attempt.")) {
					
					// Tentativa de acertar o nome
					System.out.print("Quem você acha que esse personagem é: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);
					
				} else if (instruction.contains("persona.")) {
					
					// Tentativa de acertar o nome
					System.out.print("Qual personagem você quer ser: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);
					
				} else if (instruction.contains("tip.")) {
					
					// Tentativa de acertar o nome
					System.out.print("Dica: ");
					response = reader.nextLine();
					player.getMessager().sendMessage(response);
					
				}
			} else if (instruction.contains("print.")) {
				System.out.println(instruction);
			} else {
				System.out.println("DEBUG: " + instruction);
			}
		}
	}
}
