package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository, SalvoRepository salvoRepository,
									  ScoreRepository scoreRepository) {
		return (args -> {

			Player playerJbauer = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player playerObrian = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player playerKbauer = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
			Player playerAlmeida = new Player("t.almeida@ctu.com", passwordEncoder().encode("mole"));

			Date date = new Date();

			Game game1 = new Game();
			Game game2 = new Game(Date.from(date.toInstant().plusSeconds(3600)));
			Game game3 = new Game(Date.from(game2.getCreationDate().toInstant().plusSeconds(3600)));
			Game game4 = new Game(Date.from(game3.getCreationDate().toInstant().plusSeconds(3600)));
			Game game5 = new Game(Date.from(game4.getCreationDate().toInstant().plusSeconds(3600)));
			Game game6 = new Game(Date.from(game5.getCreationDate().toInstant().plusSeconds(3600)));
			Game game7 = new Game(Date.from(game6.getCreationDate().toInstant().plusSeconds(3600)));
			Game game8 = new Game(Date.from(game7.getCreationDate().toInstant().plusSeconds(3600)));


			GamePlayer gamePlayer1 = new GamePlayer(game1, playerJbauer);
			GamePlayer gamePlayer2 = new GamePlayer(game1, playerObrian);
			GamePlayer gamePlayer3 = new GamePlayer(game2, playerJbauer);
			GamePlayer gamePlayer4 = new GamePlayer(game2, playerObrian);
			GamePlayer gamePlayer5 = new GamePlayer(game3, playerObrian);
			GamePlayer gamePlayer6 = new GamePlayer(game3, playerAlmeida);
			GamePlayer gamePlayer7 = new GamePlayer(game4, playerObrian);
			GamePlayer gamePlayer8 = new GamePlayer(game4, playerJbauer);
			GamePlayer gamePlayer9 = new GamePlayer(game5, playerAlmeida);
			GamePlayer gamePlayer10 = new GamePlayer(game5, playerJbauer);
			GamePlayer gamePlayer11 = new GamePlayer(game6, playerKbauer);
			GamePlayer gamePlayer12 = new GamePlayer(game7, playerAlmeida);
			GamePlayer gamePlayer13 = new GamePlayer(game8, playerKbauer);
			GamePlayer gamePlayer14 = new GamePlayer(game8, playerAlmeida);

			List<String> shipLocations1 = Arrays.asList("H2", "H3", "H4");
			List<String> shipLocations2 = Arrays.asList("E1", "F1", "G1");
			List<String> shipLocations3 = Arrays.asList("B4", "B5");
			List<String> shipLocations4 = Arrays.asList("B5", "C5", "D5");
			List<String> shipLocations5 = Arrays.asList("F1", "F2");
			List<String> shipLocations6 = Arrays.asList("C6", "C7");
			List<String> shipLocations7 = Arrays.asList("A2", "A3", "A4");
			List<String> shipLocations8 = Arrays.asList("G6", "H6");

			Ship ship1 = new Ship(shipLocations1, "destroyer", gamePlayer1);
			Ship ship2 = new Ship(shipLocations2, "submarine", gamePlayer1);
			Ship ship3 = new Ship(shipLocations3, "patrolboat", gamePlayer1);
			Ship ship4 = new Ship(shipLocations4, "destroyer", gamePlayer2);
			Ship ship5 = new Ship(shipLocations5, "patrolboat", gamePlayer2);
			Ship ship6 = new Ship(shipLocations4, "destroyer", gamePlayer3);
			Ship ship7 = new Ship(shipLocations6, "patrolboat", gamePlayer3);
			Ship ship8 = new Ship(shipLocations7, "submarine", gamePlayer4);
			Ship ship9 = new Ship(shipLocations8, "patrolboat", gamePlayer4);
			Ship ship10 = new Ship(shipLocations4, "destroyer", gamePlayer5);
			Ship ship11 = new Ship(shipLocations6, "patrolboat", gamePlayer5);
			Ship ship12 = new Ship(shipLocations7, "submarine", gamePlayer6);
			Ship ship13 = new Ship(shipLocations8, "patrolboat", gamePlayer6);
			Ship ship14 = new Ship(shipLocations4, "destroyer", gamePlayer7);
			Ship ship15 = new Ship(shipLocations6, "patrolboat", gamePlayer7);
			Ship ship16 = new Ship(shipLocations7, "submarine", gamePlayer8);
			Ship ship17 = new Ship(shipLocations8, "patrolboat", gamePlayer8);
			Ship ship18 = new Ship(shipLocations4, "destroyer", gamePlayer9);
			Ship ship19 = new Ship(shipLocations6, "patrolboat", gamePlayer9);
			Ship ship20 = new Ship(shipLocations7, "submarine", gamePlayer10);
			Ship ship21 = new Ship(shipLocations8, "patrolboat", gamePlayer10);
			Ship ship22 = new Ship(shipLocations4, "destroyer", gamePlayer11);
			Ship ship23 = new Ship(shipLocations6, "patrolboat", gamePlayer11);
			Ship ship24 = new Ship(shipLocations7, "submarine", gamePlayer13);
			Ship ship25 = new Ship(shipLocations8, "patrolboat", gamePlayer13);


            Salvo salvo1 = new Salvo(gamePlayer1, 1, Arrays.asList("B5", "C5", "F1"));
            Salvo salvo2 = new Salvo(gamePlayer1, 2, Arrays.asList("F2", "D5"));
            Salvo salvo3 = new Salvo(gamePlayer2, 1, Arrays.asList("B4", "B5", "B6"));
            Salvo salvo4 = new Salvo(gamePlayer2, 2, Arrays.asList("E1", "H3", "A2"));
            Salvo salvo5 = new Salvo(gamePlayer3, 1, Arrays.asList("A2", "A4", "G6"));
            Salvo salvo6 = new Salvo(gamePlayer3, 2, Arrays.asList("A3", "H6"));
            Salvo salvo7 = new Salvo(gamePlayer4, 1, Arrays.asList("B5", "D5", "C7"));
            Salvo salvo8 = new Salvo(gamePlayer4, 2, Arrays.asList("C5", "C6"));
            Salvo salvo9 = new Salvo(gamePlayer5, 1, Arrays.asList("G6", "H6", "A4"));
            Salvo salvo10 = new Salvo(gamePlayer5, 2, Arrays.asList("A2", "A3", "D8"));
            Salvo salvo11 = new Salvo(gamePlayer6, 1, Arrays.asList("H1", "H2", "H3"));
            Salvo salvo12 = new Salvo(gamePlayer6, 2, Arrays.asList("E1", "F2", "G3"));
            Salvo salvo13 = new Salvo(gamePlayer7, 1, Arrays.asList("A3", "A4", "F7"));
            Salvo salvo14 = new Salvo(gamePlayer7, 2, Arrays.asList("A2", "G6", "H6"));
            Salvo salvo15 = new Salvo(gamePlayer8, 1, Arrays.asList("B5", "B6", "C7"));
            Salvo salvo16 = new Salvo(gamePlayer8, 2, Arrays.asList("C5", "C7", "D5"));
            Salvo salvo17 = new Salvo(gamePlayer9, 1, Arrays.asList("A1", "A2", "A3"));
            Salvo salvo18 = new Salvo(gamePlayer9, 2, Arrays.asList("G6", "G7", "G8"));
            Salvo salvo19 = new Salvo(gamePlayer10, 1, Arrays.asList("B5", "B6", "B7"));
            Salvo salvo20 = new Salvo(gamePlayer10, 2, Arrays.asList("C6", "D6", "E6"));
            Salvo salvo21 = new Salvo(gamePlayer10, 3, Arrays.asList("H1", "H8"));

            Score score1 = new Score(Date.from(date.toInstant().plusSeconds(1800)), game1, playerJbauer, 1);
            Score score2 = new Score(Date.from(date.toInstant().plusSeconds(1800)), game1, playerObrian, 0);
            Score score3 = new Score(Date.from(score1.getDate().toInstant().plusSeconds(3600)), game2, playerJbauer, 0.5);
            Score score4 = new Score(Date.from(score1.getDate().toInstant().plusSeconds(3600)), game2, playerObrian, 0.5);
            Score score5 = new Score(Date.from(score3.getDate().toInstant().plusSeconds(3600)), game3, playerObrian, 1);
            Score score6 = new Score(Date.from(score3.getDate().toInstant().plusSeconds(3600)), game3, playerAlmeida, 0);
            Score score7 = new Score(Date.from(score5.getDate().toInstant().plusSeconds(3600)), game4, playerObrian, 0.5);
            Score score8 = new Score(Date.from(score5.getDate().toInstant().plusSeconds(3600)), game4, playerJbauer, 0.5);

			playerRepository.saveAll(Arrays.asList(playerJbauer, playerObrian, playerKbauer, playerAlmeida));
			gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4, game5,
					game6, game7, game8));
			gamePlayerRepository.saveAll(Arrays.asList(gamePlayer1, gamePlayer2, gamePlayer3,
					gamePlayer4, gamePlayer5, gamePlayer6, gamePlayer7, gamePlayer8, gamePlayer9,
					gamePlayer10, gamePlayer11, gamePlayer12, gamePlayer13, gamePlayer14));
			shipRepository.saveAll(Arrays.asList(ship1, ship2, ship3, ship4, ship5, ship6,
					ship7, ship8, ship9, ship10, ship11, ship12, ship13, ship14, ship15, ship16,
					ship17, ship18, ship19, ship20, ship21, ship22, ship23, ship24, ship25));
			salvoRepository.saveAll(Arrays.asList(salvo1, salvo2, salvo3, salvo4, salvo5,
					salvo6, salvo7, salvo8, salvo9, salvo10, salvo11, salvo12, salvo13,
					salvo14, salvo15, salvo16, salvo17, salvo18, salvo19, salvo20, salvo21));
			scoreRepository.saveAll(Arrays.asList(score1, score2, score3, score4, score5,
					score6, score7, score8));
		});
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userName-> {
			Player player = playerRepository.findByUserName(userName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + userName);
			}
		});
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/rest/**").hasAuthority("USER")
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/**").permitAll()
				.anyRequest().permitAll();

		http.headers().frameOptions().disable();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		http.csrf().disable();

		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
