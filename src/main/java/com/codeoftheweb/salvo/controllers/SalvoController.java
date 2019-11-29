package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.SalvoRepository;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    SalvoRepository salvoRepository;

    @RequestMapping(path = "/games/players/{gamePlayerID}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvo(@PathVariable Long gamePlayerID, @RequestBody Salvo salvo, Authentication authentication) {
        if (Util.isGuest(authentication))
            return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerID).get();
        if (Objects.isNull(gamePlayer)) {
            return new ResponseEntity<>(Util.makeMap("error", "GamePlayer does not exist"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.getPlayer().getId() != Util.getPlayerFromAuthentication(authentication, playerRepository).getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "Player ID does not match link ID"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayer.salvoExistForTurn(salvo.getTurn())) {
            return new ResponseEntity<>(Util.makeMap("error", "Salvo already submitted for this turn"), HttpStatus.FORBIDDEN);
        }
        if (salvo.getSalvoLocations().size() != 5) {
            return new ResponseEntity<>(Util.makeMap("error", "Salvo should fire 5 times"), HttpStatus.FORBIDDEN);
        }
        salvo.setGamePlayer(gamePlayer);
        salvo.setTurn(gamePlayer.getNextTurn());
        salvoRepository.save(salvo);
        return new ResponseEntity<>(Util.makeMap("OK", "Salvoes thrown"), HttpStatus.CREATED);
    }

}
