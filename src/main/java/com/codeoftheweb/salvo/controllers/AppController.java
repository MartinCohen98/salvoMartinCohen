package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private ScoreRepository scoreRepository;

    @RequestMapping("/game_view/{gamePlayerID}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable Long gamePlayerID, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        if (Util.isGuest(authentication))
            return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        if (gamePlayer.getPlayer().getId() != Util.getPlayerFromAuthentication(authentication, playerRepository).getId())
            return new ResponseEntity<>(Util.makeMap("error", "User ID does not match link ID"), HttpStatus.UNAUTHORIZED);
        Map<String, Object> map = gamePlayer.getGame().makeGameDTO();
        map.put("gameState", this.getState(gamePlayer));
        map.put("ships", gamePlayer.makeShipsDTO());
        map.put("salvoes", gamePlayer.getGame().makeSalvoesDTO());
        map.put("hits", this.makeHitsDTO(gamePlayer, gamePlayer.getOpponent()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {
        if (Util.isGuest(authentication))
            return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        Game game = gameRepository.findById(gameID).get();
        if (Objects.isNull(game)) {
            return new ResponseEntity<>(Util.makeMap("error", "Game does not exist"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() != 1) {
            return new ResponseEntity<>(Util.makeMap("error", "Game is not joinable"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(game, Util.getPlayerFromAuthentication(authentication, playerRepository));
        gamePlayerRepository.save(gamePlayer);
        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        Comparator<Map<String, Object>> byScore =
                Comparator.comparing(map -> ((double) map.get("score")));
        List<Map<String, Object>> list = playerRepository.findAll().stream()
                .map(Player::getLeaderboardDTO)
                .collect(Collectors.toList());
        list.sort(byScore);
        Collections.reverse(list);
        return list;
    }

    private String getState(GamePlayer gamePlayer) {
        if (gamePlayer.getShips().isEmpty())
            return "PLACESHIPS";
        if (gamePlayer.getGame().getGamePlayers().size() == 1)
            return "WAITINGFOROPP";
        GamePlayer opponent = gamePlayer.getOpponent();
        if ((gamePlayer.lostAllShips() && opponent.lostAllShips()) &&
                (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size())) {
            Util.setScoreToTie(gamePlayer, scoreRepository);
            return "TIE";
        }
        if (gamePlayer.lostAllShips() && (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size())) {
            Util.setScoreToLoss(gamePlayer, scoreRepository);
            return "LOST";
        }
        if (opponent.lostAllShips() && (gamePlayer.getSalvoes().size() == opponent.getSalvoes().size())) {
            Util.setScoreToWin(gamePlayer, scoreRepository);
            return "WON";
        }
        if (gamePlayer.getSalvoes().size() <= opponent.getSalvoes().size())
            if (!opponent.getShips().isEmpty())
                return "PLAY";
        return "WAIT";
    }

    private Map<String, Object> makeHitsDTO(GamePlayer gamePlayer, GamePlayer opponent) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("self", gamePlayer.makeHitsDTO(opponent.getSalvoes()));
        map.put("opponent", opponent.makeHitsDTO(gamePlayer.getSalvoes()));
        return map;
    }
}
