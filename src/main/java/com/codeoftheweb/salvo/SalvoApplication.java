package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository) {
		return (args -> {

			Player player1 = new Player("jackbauer@gmail.com");
			Player player2 = new Player("michelledessler@gmail.com");

			Date date = new Date();

			Game game1 = new Game();
			Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(date.toInstant().plusSeconds(7200)));

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
			GamePlayer gamePlayer3 = new GamePlayer(game2, player1);

			//Agrega algunos jugadores
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(new Player("davidpalmer@hotmail.com"));

			//Agrega algunos partidas
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);
		});
	}
}
