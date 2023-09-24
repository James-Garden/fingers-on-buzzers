package io.fingersonbuzzers.api.game;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "Game", timeToLive = Game.INITIAL_TIME_TO_LIVE)
public class Game {

  static final long INITIAL_TIME_TO_LIVE = 1800L;

  @TimeToLive
  private Long timeToLive;

  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private Player host;
  private Set<Player> players;

  public Game(Player host) {
    this.host = host;
    this.players = new HashSet<>();
  }

  public UUID getId() {
    return id;
  }

  public Player getHost() {
    return host;
  }

  public void setHost(Player host) {
    this.host = host;
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public void setPlayers(Set<Player> players) {
    this.players = players;
  }

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public void resetTimeToLive() {
    this.timeToLive = INITIAL_TIME_TO_LIVE;
  }
}
