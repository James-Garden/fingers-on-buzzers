package io.fingersonbuzzers.api.game;

import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class GameUpdatedEvent extends ApplicationEvent {

  private final UUID gameId;

  public GameUpdatedEvent(Object source, UUID gameId) {
    super(source);
    this.gameId = gameId;
  }

  public UUID getGameId() {
    return gameId;
  }
}
