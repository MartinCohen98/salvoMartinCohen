package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores;

    private Date creationDate;

    public Game() {
        creationDate = new Date();
    }

    public Game(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", this.getId());
        map.put("created", this.getCreationDate().getTime());
        map.put("gamePlayers", this.getGamePlayers()
                .stream().map(gamePlayer -> gamePlayer.makeGamePlayerDTO())
                .collect(Collectors.toList()));
        map.put("scores", this.getScores()
                .stream().map(score -> score.makeScoreDTO())
                .collect(Collectors.toList()));
        return map;
    }

    public List<Object> makeSalvoesDTO() {
        return this.getGamePlayers().stream()
                .flatMap(gamePlayer -> gamePlayer.getSalvoes()
                        .stream().map(salvo -> salvo.makeSalvoDTO()))
                        .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<GamePlayer> getGamePlayers() {
        return (gamePlayers.stream().collect(Collectors.toList()));
    }

    public List<Score> getScores() {
        return scores.stream().collect(Collectors.toList());
    }
}
