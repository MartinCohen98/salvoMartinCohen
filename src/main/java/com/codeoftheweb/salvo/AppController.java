package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public Map<String, Object> getGameAll(Authentication authentication) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<Object> list = gameRepository.findAll()
                .stream().map(game -> game.makeGameDTO())
                .collect(Collectors.toList());
        if (!this.isGuest(authentication)) {
            map.put("player", this.getPlayerFromAuthentication(authentication).makePlayerDTO());
        } else {
            map.put("player", "Guest");
        }
        map.put("games", list);
        return map;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (this.isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        Game game = new Game();
        GamePlayer gamePlayer = new GamePlayer(game, this.getPlayerFromAuthentication(authentication));
        gameRepository.save(game);
        gamePlayerRepository.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{gamePlayerID}")
    public ResponseEntity<Map<String, Object>> getGame(@PathVariable Long gamePlayerID, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        if (this.isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        if (gamePlayer.getPlayer().getId() != getPlayerFromAuthentication(authentication).getId())
            return new ResponseEntity<>(makeMap("error", "User ID does not match link ID"), HttpStatus.UNAUTHORIZED);
        Map<String, Object> map = gamePlayer.getGame().makeGameDTO();
        map.put("gameState", this.getState(gamePlayer, gamePlayer.getOpponent()));
        map.put("ships", gamePlayer.makeShipsDTO());
        map.put("salvoes", gamePlayer.getGame().makeSalvoesDTO());
        map.put("hits", this.makeHitsDTO(gamePlayer, gamePlayer.getOpponent()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private Map<String, Object> makeHitsDTO(GamePlayer gamePlayer, GamePlayer opponent) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("self", gamePlayer.makeHitsDTO(opponent.getSalvoes()));
        map.put("opponent", opponent.makeHitsDTO(gamePlayer.getSalvoes()));
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
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(email) !=  null) {
            return new ResponseEntity<>("Username already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {
        if (this.isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        Game game = gameRepository.findById(gameID).get();
        if (Objects.isNull(game)) {
            return new ResponseEntity<>(makeMap("error", "Game does not exist"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() != 1) {
            return new ResponseEntity<>(makeMap("error", "Game is not joinable"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(game, this.getPlayerFromAuthentication(authentication));
        gamePlayerRepository.save(gamePlayer);
        return new ResponseEntity<>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerID}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShip(@PathVariable Long gamePlayerID, @RequestBody List<Ship> ships,
                                                        Authentication authentication) {
        if (this.isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        if (Objects.isNull(gamePlayer)) {
            return new ResponseEntity<>(makeMap("error", "GamePlayer does not exist"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != this.getPlayerFromAuthentication(authentication).getId()) {
            return new ResponseEntity<>(makeMap("error", "Player ID does not match link ID"), HttpStatus.UNAUTHORIZED);
        }
        if (!gamePlayer.getShips().isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "GamePlayer already has ships"), HttpStatus.FORBIDDEN);
        }
        ships.stream().forEach(ship -> ship.setGamePlayer(gamePlayer));
        shipRepository.saveAll(ships);
        return new ResponseEntity<>(makeMap("OK", "Ships placed"), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerID}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable Long gamePlayerID, @RequestBody Salvo salvo, Authentication authentication) {
        if (this.isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        if (Objects.isNull(gamePlayer)) {
            return new ResponseEntity<>(makeMap("error", "GamePlayer does not exist"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != this.getPlayerFromAuthentication(authentication).getId()) {
            return new ResponseEntity<>(makeMap("error", "Player ID does not match link ID"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.salvoExistForTurn(salvo.getTurn())) {
            return new ResponseEntity<>(makeMap("error", "Salvo already submitted for this turn"), HttpStatus.FORBIDDEN);
        }
        if (salvo.getSalvoLocations().size() != 5) {
            return new ResponseEntity<>(makeMap("error", "Salvo should fire 5 times"), HttpStatus.FORBIDDEN);
        }
        salvo.setGamePlayer(gamePlayer);
        salvo.setTurn(gamePlayer.getNextTurn());
        salvoRepository.save(salvo);
        return new ResponseEntity<>(makeMap("OK", "Salvoes thrown"), HttpStatus.CREATED);
    }

    private Player getPlayerFromAuthentication(Authentication authentication) {
        return (playerRepository.findByUserName(authentication.getName()));
    }

    private boolean isGuest(Authentication authentication) {
        return Objects.isNull(authentication) || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String string, Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(string, object);
        return map;
    }

    private String getState(GamePlayer gamePlayer, GamePlayer opponent) {
        if (gamePlayer.getShips().isEmpty())
            return "PLACESHIPS";
        if (gamePlayer.getGame().getGamePlayers().size() == 1)
            return "WAITINGFOROPP";
        if (gamePlayer.getId() < opponent.getId())
            return "PLAY";
        if (gamePlayer.getId() > opponent.getId())
            return "WAIT";
        return "WAIT";
    }
}
