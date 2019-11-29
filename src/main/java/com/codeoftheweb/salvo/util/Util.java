package com.codeoftheweb.salvo.util;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

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
}
