package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository) {
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

			List<String> shipLocations1 = new LinkedList<>();
			shipLocations1.add("H3");
			shipLocations1.add("H4");
			shipLocations1.add("H5");

			List<String> shipLocations2 = new LinkedList<>();
			shipLocations2.add("B6");
			shipLocations2.add("C6");
			shipLocations2.add("D6");

			List<String> shipLocations3 = new LinkedList<>();
			shipLocations3.add("A4");
			shipLocations3.add("A5");

			Ship ship1 = gamePlayer1.addShip(shipLocations1, "Submarine");
			Ship ship2 = gamePlayer1.addShip(shipLocations2, "Destroyer");
			Ship ship3 = gamePlayer2.addShip(shipLocations3, "Patrol Boat");

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(new Player("davidpalmer@hotmail.com"));

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);
			gamePlayerRepository.save(gamePlayer3);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
		});
	}
}
