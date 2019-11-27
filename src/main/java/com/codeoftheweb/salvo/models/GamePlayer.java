package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes;

    private Date joinDate;


    public GamePlayer() {
        joinDate = new Date();
    }

    public GamePlayer(Game game, Player player) {
        this.player = player;
        this.game = game;
        joinDate = new Date();
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", this.getId());
        map.put("player", this.getPlayer().makePlayerDTO());
        return map;
    }

    public  List<Object> makeShipsDTO() {
        return this.getShips()
                .stream().map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    public boolean salvoExistForTurn(int turn) {
        return salvoes.stream().anyMatch(salvo -> salvo.getTurn() == turn);
    }

    public Score getScore() {
        return player.getScore(game);
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public List<Ship> getShips() {
        return ships.stream().collect(Collectors.toList());
    }

    public List<Salvo> getSalvoes() {
        return salvoes.stream().collect(Collectors.toList());
    }
}
