package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.PlayerRepository;
import io.fingersonbuzzers.backend.validation.ValidationResult;
import io.micrometer.common.util.StringUtils;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class LobbyFormValidator {

  private final PlayerRepository playerRepository;

  public LobbyFormValidator(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public ValidationResult validate(LobbyForm form) {
    var errors = new ValidationResult();

    if (StringUtils.isBlank(form.playerName())) {
      errors.reject(LobbyForm.PLAYER_NAME_FIELD, "required");
    }

    if (Objects.nonNull(form.playerId())
        && !playerRepository.existsById(form.playerId())) {
      errors.reject(LobbyForm.PLAYER_ID_FIELD, "invalid");
    }

    return errors;
  }
}
