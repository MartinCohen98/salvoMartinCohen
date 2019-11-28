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

    public List<Object> makeHitsDTO(List<Salvo> opponentSalvoes) {
        if (this.getId() != 0)
            return opponentSalvoes.stream()
                .map(salvo -> this.makeHitDTO(salvo, opponentSalvoes))
                .collect(Collectors.toList());
        else
            return new LinkedList<>();
    }

    private Map<String, Object> makeHitDTO(Salvo salvo, List<Salvo> opponenSalvoes) {
        Map<String, Object> map = new LinkedHashMap<>();
        List<String> hitLocations = salvo.getHitLocations(this.getShips());
        map.put("turn", salvo.getTurn());
        map.put("hitLocations", hitLocations);
        map.put("damages", this.getDamagesDTO(opponenSalvoes, salvo));
        map.put("missed", salvo.getSalvoLocations().size() - hitLocations.size());
        return map;
    }

    private Map<String, Object> getDamagesDTO(List<Salvo> opponentSalvoes, Salvo salvo) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("carrierHits", salvo.getHitsOnShipType(this.getShips(), "carrier"));
        map.put("battleshipHits", salvo.getHitsOnShipType(this.getShips(), "battleship"));
        map.put("submarineHits", salvo.getHitsOnShipType(this.getShips(), "submarine"));
        map.put("destroyerHits", salvo.getHitsOnShipType(this.getShips(), "destroyer"));
        map.put("patrolboatHits", salvo.getHitsOnShipType(this.getShips(), "patrolboat"));
        map.put("carrier", this.getHitsBeforeTurnForType(opponentSalvoes, "carrier", salvo.getTurn()));
        map.put("battleship", this.getHitsBeforeTurnForType(opponentSalvoes, "battleship", salvo.getTurn()));
        map.put("submarine", this.getHitsBeforeTurnForType(opponentSalvoes, "submarine", salvo.getTurn()));
        map.put("destroyer", this.getHitsBeforeTurnForType(opponentSalvoes, "destroyer", salvo.getTurn()));
        map.put("patrolboat", this.getHitsBeforeTurnForType(opponentSalvoes, "patrolboat", salvo.getTurn()));
        return map;
    }

    private long getHitsBeforeTurnForType(List<Salvo> opponentSalvoes, String type, int turn) {
        Ship ship = ships.stream().filter(ship1 -> ship1.getType() == type).findFirst().orElse(new Ship());
        List<Salvo> salvosToCount = opponentSalvoes.stream()
                .filter(salvo -> salvo.getTurn() <= turn)
                .collect(Collectors.toList());
        return salvosToCount.stream().mapToLong(salvo -> salvo.getHitsOnShip(ship)).sum();
    }

    public  List<Object> makeShipsDTO() {
        return this.getShips()
                .stream().map(ship -> ship.makeShipDTO())
                .collect(Collectors.toList());
    }

    public boolean salvoExistForTurn(int turn) {
        return salvoes.stream().anyMatch(salvo -> salvo.getTurn() == turn);
    }

    public int getNextTurn() {
        return (salvoes.size() + 1);
    }

    public GamePlayer getOpponent() {
        return this.getGame().getGamePlayers().stream()
                .filter(gamePlayer -> gamePlayer.getId() != this.getId())
                .findFirst().orElse(new GamePlayer());
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
        if (!Objects.isNull(ships))
            return ships.stream().collect(Collectors.toList());
        else
            return new LinkedList<>();
    }

    public List<Salvo> getSalvoes() {
        if (!Objects.isNull(salvoes))
            return salvoes.stream().collect(Collectors.toList());
        else
            return new LinkedList<>();
    }
}
