package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public List<Object> getGameAll() {
        return gameRepository.findAll()
                .stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerID}")
    public Map<String, Object> getGame(@PathVariable Long gamePlayerID) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        Map<String, Object> map = gamePlayer.getGame().makeGameDTO();
        map.put("ships", gamePlayer.makeShipsDTO());
        map.put("salvoes", gamePlayer.getGame().makeSalvoesDTO());
        return map;
    }

    @RequestMapping("leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        Comparator<Map<String, Object>> byScore =
                Comparator.comparing(map -> ((double) map.get("score")));
        List<Map<String, Object>> list = playerRepository.findAll().stream()
                .map(player -> player.getLeaderboardDTO())
                .collect(Collectors.toList());
        list.sort(byScore);
        Collections.reverse(list);
        return list;
    }
}
