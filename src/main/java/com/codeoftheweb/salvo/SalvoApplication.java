package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repository) {
		return (args -> {
			repository.save(new Player("jackbauer@gmail.com"));
			repository.save(new Player("michelledessler@gmail.com"));
			repository.save(new Player("davidpalmer@hotmail.com"));
		});
	}
}
