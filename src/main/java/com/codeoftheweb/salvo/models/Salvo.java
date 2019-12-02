package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    @ElementCollection
    @Column(name = "location")
    private List<String> salvoLocations;


    public Salvo() {}

    public Salvo(GamePlayer gamePlayer, int turn, List<String> salvoLocations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("turn", this.getTurn());
        map.put("player", this.getGamePlayer().getPlayer().getId());
        map.put("locations", this.getSalvoLocations());
        return map;
    }

    public long getHitsOnShipType(List<Ship> ships, String type) {
        Ship ship = ships.stream().filter(ship1 -> ship1.getType().equals(type)).findFirst().orElse(new Ship());
        return this.getHitsOnShip(ship);
    }

    public List<String> getHitLocations(List<Ship> ships) {
        return ships.stream()
                .flatMap(ship -> ship.getShipLocations().stream())
                .filter(location -> (salvoLocations.contains(location)))
                .collect(Collectors.toList());
    }

    private long getHitsOnShip(Ship ship) {
        if (ship.getId()!= 0)
            return salvoLocations.stream()
                .filter(location -> ship.getShipLocations().contains(location))
                .count();
        else
            return 0;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }
}
