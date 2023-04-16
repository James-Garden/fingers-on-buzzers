package io.fingersonbuzzers.backend.lobby;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.validation.ValidationResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LobbyResponse(LobbyDto lobbyData,
                            PlayerDto playerData,
                            ValidationResult errors) {
  public static LobbyResponse success(LobbyDto lobbyData,
                                      PlayerDto playerDto) {
    return new LobbyResponse(lobbyData, playerDto, null);
  }

  public static LobbyResponse failure(ValidationResult errors) {
    return new LobbyResponse(null, null, errors);
  }
}
