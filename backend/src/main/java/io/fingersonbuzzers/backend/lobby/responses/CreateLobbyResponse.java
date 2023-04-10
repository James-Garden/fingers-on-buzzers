package io.fingersonbuzzers.backend.lobby.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fingersonbuzzers.backend.validation.ValidationResult;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateLobbyResponse(UUID lobbyId,
                                  UUID playerId,
                                  ValidationResult errors) {
  public static CreateLobbyResponse success(UUID lobbyId, UUID playerId) {
    return new CreateLobbyResponse(lobbyId, playerId, null);
  }

  public static CreateLobbyResponse failure(ValidationResult validationResult) {
    return new CreateLobbyResponse(null, null, validationResult);
  }
}
