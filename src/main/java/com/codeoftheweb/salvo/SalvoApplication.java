package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
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
                                      ShipRepository shipRepository, SalvoRepository salvoRepository) {
		return (args -> {

			Player player1 = new Player("jackbauer@gmail.com");
			Player player2 = new Player("michelledessler@gmail.com");
			Player player3 = new Player("davidpalmer@hotmail.com");

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

			List<String> salvoLocations1 = new LinkedList<>();
			salvoLocations1.add("A5");
			salvoLocations1.add("E1");

			List<String> salvoLocations2 = new LinkedList<>();
			salvoLocations2.add("A7");
			salvoLocations2.add("G3");

			List<String> salvoLocations3 = new LinkedList<>();
			salvoLocations3.add("B2");
			salvoLocations3.add("H3");

            Salvo salvo1 = new Salvo(gamePlayer1, 1, salvoLocations1);
            Salvo salvo2 = new Salvo(gamePlayer1, 2, salvoLocations2);
            Salvo salvo3 = new Salvo(gamePlayer2, 1, salvoLocations3);

			List<Player> playerList = new LinkedList<>();
			playerList.add(player1);
			playerList.add(player2);
			playerList.add(player3);

			List<Game> gameList = new LinkedList<>();
			gameList.add(game1);
			gameList.add(game2);
			gameList.add(game3);

			List<GamePlayer> gamePlayerList = new LinkedList<>();
			gamePlayerList.add(gamePlayer1);
			gamePlayerList.add(gamePlayer2);
			gamePlayerList.add(gamePlayer3);

			List<Ship> shipList = new LinkedList<>();
			shipList.add(ship1);
			shipList.add(ship2);
			shipList.add(ship3);

			List<Salvo> salvoList = new LinkedList<>();
			salvoList.add(salvo1);
			salvoList.add(salvo2);
			salvoList.add(salvo3);

			playerRepository.saveAll(playerList);
			gameRepository.saveAll(gameList);
			gamePlayerRepository.saveAll(gamePlayerList);
			shipRepository.saveAll(shipList);
			salvoRepository.saveAll(salvoList);
		});
	}
}
