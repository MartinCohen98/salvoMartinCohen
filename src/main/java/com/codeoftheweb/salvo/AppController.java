package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public Object getGameAll(Authentication authentication) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<Object> list = gameRepository.findAll()
                .stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
        if (!Objects.isNull(authentication)) {
            map.put("player", this.getPlayerFromAuthentication(authentication).makePlayerDTO());
            map.put("games", list);
            return map;
        }
        return list;
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

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String username, @RequestParam String password) {

        if (username.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(username) !=  null) {
            return new ResponseEntity<>("Username already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(username, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Player getPlayerFromAuthentication(Authentication authentication) {
        return (playerRepository.findByUserName(authentication.getName()));
    }
}
