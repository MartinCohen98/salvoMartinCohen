package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;

    private String userName;

    private String password;


    public Player() {}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", this.getId());
        map.put("email", this.getUserName());
        return map;
    }

    public Score getScore(Game game) {
        return scores.stream()
                .filter(score -> score.getGame().getId() == game.getId())
                .findFirst().orElse(null);
    }

    public Map<String, Object> getLeaderboardDTO() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", this.getId());
        map.put("email", this.getUserName());
        map.put("score", this.getTotalScore());
        map.put("wins", this.getWins());
        map.put("losses", this.getLosses());
        map.put("ties", this.getTies());
        return map;
    }

    private long getWins() {
        return countScoresWithNumber(1);
    }

    private long getLosses() {
        return countScoresWithNumber(0);
    }

    private long getTies() {
        return countScoresWithNumber(0.5);
    }

    private long countScoresWithNumber(double number) {
        return scores.stream().filter(score -> score.getScore() == number).count();
    }

    public double getTotalScore() {
        return scores.stream().mapToDouble(Score::getScore).sum();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Game> getGames() {
        return (gamePlayers.stream().map(gamePlayer -> gamePlayer.getGame()).collect(Collectors.toList()));
    }

    public long getId() {
        return id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public String getPassword() {
        return password;
    }
}
