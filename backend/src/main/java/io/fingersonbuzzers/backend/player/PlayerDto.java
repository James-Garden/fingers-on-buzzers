package io.fingersonbuzzers.backend.player;

import java.util.UUID;

public record PlayerDto(UUID playerId,
                        String playerName) {

  public static PlayerDto from(Player player) {
    return new PlayerDto(player.getId(), player.getName());
  }
}
