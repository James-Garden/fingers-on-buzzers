package io.fingersonbuzzers.backend.lobby;

import java.util.UUID;

public record LobbyDto(UUID lobbyId) {

  public static LobbyDto from(Lobby lobby) {
    return new LobbyDto(lobby.getId());
  }
}
