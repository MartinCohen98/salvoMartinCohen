package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.com.codeoftheweb.salvo.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Object> getGames() {
        return gameRepository.findAll().stream().map(game -> gameToMap(game)).collect(Collectors.toList());
    }

    private Map<String, Object> gameToMap(Game game) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", game.getId());
        map.put("created", game.getCreationDate());
        map.put("gamePlayers", gamePlayersFromGame(game));
        return map;
    }

    private List<Object> gamePlayersFromGame(Game game) {
        return (game.getGamePlayers().stream().map(gamePlayer -> gamePlayerToMap(gamePlayer)).collect(Collectors.toList()));
    }

    private Map<String, Object> gamePlayerToMap(GamePlayer gamePlayer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", gamePlayer.getId());
        map.put("player", playerToMap(gamePlayer.getPlayer()));
        return map;
    }

    private Map<String, Object> playerToMap(Player player) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", player.getId());
        map.put("email", player.getUserName());
        return map;
    }
}
