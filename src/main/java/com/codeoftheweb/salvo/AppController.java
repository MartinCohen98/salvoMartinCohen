package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Object> getGameAll() {
        return gameRepository.findAll()
                .stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
    }
}
