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

	public void run() throws IOException, InterruptedException {
		String instruction, response;
		while (true) {
			System.out.println("Esperando por instruções");
			instruction = player.getMessager().receiveMessage();

			if (instruction.contains(player.getNickname() + ".")) {
				if (instruction.contains("question.")) {
					// Pergunta para o personagem da rodada
					System.out.println("Faça uma pergunta: ");
					response = reader.next();
					player.getMessager().sendMessage(response);
				} else if (instruction.contains("answer.")) {
					// Resposta a pergunta feita nesta rodada
					System.out.println("Digite (S)im ou (N)ao: ");
					response = reader.next();
					player.getMessager().sendMessage(response);
				} else if (instruction.contains("attempt.")) {
					// Tentativa de acertar o nome
					System.out.println("Quem você acha que esse personagem é: ");
					response = reader.next();
					player.getMessager().sendMessage(response);
				}
			} else if (instruction.contains("print.")) {
				System.out.println(instruction);
			}
		}
	}
}
