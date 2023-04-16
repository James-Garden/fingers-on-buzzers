package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.PlayerRepository;
import io.fingersonbuzzers.backend.validation.ValidationResult;
import io.micrometer.common.util.StringUtils;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LobbyFormValidator {

  private final PlayerRepository playerRepository;
  private final LobbyRepository lobbyRepository;

  public LobbyFormValidator(PlayerRepository playerRepository,
                            LobbyRepository lobbyRepository) {
    this.playerRepository = playerRepository;
    this.lobbyRepository = lobbyRepository;
  }

  public ValidationResult validate(LobbyForm form) {
    var errors = new ValidationResult();

    validatePlayerName(form, errors);

    if (Objects.nonNull(form.playerId())) {
      validatePlayerId(form, errors);
    }

    return errors;
  }

  public ValidationResult validateWithLobby(LobbyForm form) {
    var errors = validate(form);

    validateLobbyId(form, errors);

    return errors;
  }

  private void validateLobbyId(LobbyForm form, ValidationResult errors) {
    if (Objects.isNull(form.lobbyId())) {
      errors.reject(LobbyForm.LOBBY_ID_FIELD, "required");
    } else if (!lobbyRepository.existsById(form.lobbyId())) {
      errors.reject(LobbyForm.LOBBY_ID_FIELD, "invalid");
    }
  }

  private void validatePlayerName(LobbyForm form, ValidationResult errors) {
    if (StringUtils.isBlank(form.playerName())) {
      errors.reject(LobbyForm.PLAYER_NAME_FIELD, "required");
    }
  }

  private void validatePlayerId(LobbyForm form, ValidationResult errors) {
    if (!playerRepository.existsById(form.playerId())) {
      errors.reject(LobbyForm.PLAYER_ID_FIELD, "invalid");
    }
  }
}
