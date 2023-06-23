package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.PlayerRepository;
import io.fingersonbuzzers.backend.validation.ValidationFailedException;
import io.micrometer.common.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
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

  public void validate(LobbyForm form, LobbyForm.FormType formType) throws ValidationFailedException {
    var errors = new HashMap<String, String>();

    validatePlayerName(form, errors);
    validatePlayerId(form, errors);

    if (LobbyForm.FormType.JOIN_LOBBY.equals(formType)) {
      validateLobbyId(form, errors);
    }

    if (!errors.isEmpty()) {
      throw new ValidationFailedException(errors);
    }
  }

  private void validateLobbyId(LobbyForm form, Map<String, String> errors) {
    if (Objects.isNull(form.lobbyId())) {
      errors.put(LobbyForm.LOBBY_ID_FIELD, "lobbyId must not be null");
    } else if (!lobbyRepository.existsById(form.lobbyId())) {
      errors.put(LobbyForm.LOBBY_ID_FIELD, "%s is not a valid lobbyId".formatted(form.lobbyId()));
    }
  }

  private void validatePlayerName(LobbyForm form, Map<String, String> errors) {
    if (StringUtils.isBlank(form.playerName())) {
      errors.put(LobbyForm.PLAYER_NAME_FIELD, "playerName must not be blank");
    }
  }

  private void validatePlayerId(LobbyForm form, Map<String, String> errors) {
    // playerId is not required, one will be created if null
    if (Objects.isNull(form.playerId())) {
      return;
    }

    if (!playerRepository.existsById(form.playerId())) {
      errors.put(LobbyForm.PLAYER_ID_FIELD, "%s is not a valid playerId".formatted(form.playerId()));
    }
  }
}
