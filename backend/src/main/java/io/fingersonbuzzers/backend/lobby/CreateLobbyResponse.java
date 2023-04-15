package io.fingersonbuzzers.backend.lobby;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.validation.ValidationResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateLobbyResponse(LobbyDto lobbyData,
                                  PlayerDto playerData,
                                  ValidationResult errors) {
  public static CreateLobbyResponse success(LobbyDto lobbyData,
                                            PlayerDto playerDto) {
    return new CreateLobbyResponse(lobbyData, playerDto, null);
  }

  public static CreateLobbyResponse failure(ValidationResult errors) {
    return new CreateLobbyResponse(null, null, errors);
  }
}
