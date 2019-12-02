package com.codeoftheweb.salvo.util;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Score;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Util {

    public static boolean isGuest(Authentication authentication) {
        return Objects.isNull(authentication) || authentication instanceof AnonymousAuthenticationToken;
    }

    public static Player getPlayerFromAuthentication(Authentication authentication,
                                                     PlayerRepository playerRepository) {
        return (playerRepository.findByUserName(authentication.getName()));
    }

    public static Map<String, Object> makeMap(String string, Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(string, object);
        return map;
    }

    public static void setScoreToWin(GamePlayer gamePlayer, ScoreRepository scoreRepository) {
        GamePlayer opponent = gamePlayer.getOpponent();
        if (gamePlayer.getGame().getScores().isEmpty()) {
            scoreRepository.save(new Score(new Date(), gamePlayer.getGame(), gamePlayer.getPlayer(), 1));
            scoreRepository.save(new Score(new Date(), opponent.getGame(), opponent.getPlayer(), 0));
        }
    }

    public static void setScoreToLoss(GamePlayer gamePlayer, ScoreRepository scoreRepository) {
        GamePlayer opponent = gamePlayer.getOpponent();
        if (gamePlayer.getGame().getScores().isEmpty()) {
            scoreRepository.save(new Score(new Date(), gamePlayer.getGame(), gamePlayer.getPlayer(), 0));
            scoreRepository.save(new Score(new Date(), opponent.getGame(), opponent.getPlayer(), 1));
        }
    }

    public static void setScoreToTie(GamePlayer gamePlayer, ScoreRepository scoreRepository) {
        GamePlayer opponent = gamePlayer.getOpponent();
        if (gamePlayer.getGame().getScores().isEmpty()) {
            scoreRepository.save(new Score(new Date(), gamePlayer.getGame(), gamePlayer.getPlayer(), 0.5));
            scoreRepository.save(new Score(new Date(), opponent.getGame(), opponent.getPlayer(), 0.5));
        }
    }
}
