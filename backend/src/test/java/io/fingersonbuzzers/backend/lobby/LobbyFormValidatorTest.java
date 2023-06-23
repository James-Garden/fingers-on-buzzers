package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.PlayerRepository;
import io.fingersonbuzzers.backend.utils.ValidationTestUtil;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyFormValidatorTest {

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private LobbyRepository lobbyRepository;

  @InjectMocks
  private LobbyFormValidator lobbyFormValidator;

  @BeforeEach
  void setup() {
    lobbyFormValidator = spy(lobbyFormValidator);
  }

  @Test
  void validate_ValidForm_AssertNoErrors() {
    var form = new LobbyForm("test name", null, null);

    ValidationTestUtil.assertPassesValidation(() -> lobbyFormValidator.validate(form, LobbyForm.FormType.CREATE_LOBBY));
  }

  @ParameterizedTest
  @ValueSource(strings = "        ")
  @NullAndEmptySource
  void validate_MissingPlayerName_AssertError(String blankPlayerName) {
    var form = new LobbyForm(blankPlayerName, null, null);

    ValidationTestUtil.assertFailsValidationWithErrors(
        () -> lobbyFormValidator.validate(form, LobbyForm.FormType.CREATE_LOBBY),
        Map.of("playerName", "playerName must not be blank")
    );
  }

  @Test
  void validate_InvalidPlayerId_AssertError() {
    var form = new LobbyForm("test name", UUID.randomUUID(), null);

    when(playerRepository.existsById(form.playerId())).thenReturn(false);

    ValidationTestUtil.assertFailsValidationWithErrors(
        () -> lobbyFormValidator.validate(form, LobbyForm.FormType.CREATE_LOBBY),
        Map.of(LobbyForm.PLAYER_ID_FIELD, "%s is not a valid playerId".formatted(form.playerId()))
    );
  }

  @Test
  void validate_ValidPlayerId_AssertNoErrors() {
    var form = new LobbyForm("test name", UUID.randomUUID(), null);

    when(playerRepository.existsById(form.playerId())).thenReturn(true);

    ValidationTestUtil.assertPassesValidation(() -> lobbyFormValidator.validate(form, LobbyForm.FormType.CREATE_LOBBY));
  }

  @Test
  void validateWithLobby_MissingLobbyId() {
    var form = new LobbyForm("test name", UUID.randomUUID(), null);

    when(playerRepository.existsById(form.playerId())).thenReturn(true);

    ValidationTestUtil.assertFailsValidationWithErrors(
        () -> lobbyFormValidator.validate(form, LobbyForm.FormType.JOIN_LOBBY),
        Map.of(LobbyForm.LOBBY_ID_FIELD, "lobbyId must not be null")
    );
  }

  @Test
  void validateWithLobby_InvalidLobbyId() {
    var form = new LobbyForm("test name", UUID.randomUUID(), UUID.randomUUID());

    when(playerRepository.existsById(form.playerId())).thenReturn(true);
    when(lobbyRepository.existsById(form.lobbyId())).thenReturn(false);

    ValidationTestUtil.assertFailsValidationWithErrors(
        () -> lobbyFormValidator.validate(form, LobbyForm.FormType.JOIN_LOBBY),
        Map.of(LobbyForm.LOBBY_ID_FIELD, "%s is not a valid lobbyId".formatted(form.lobbyId()))
    );
  }

  @Test
  void validateWithLobby_ValidForm() {
    var form = new LobbyForm("test name", UUID.randomUUID(), UUID.randomUUID());

    when(playerRepository.existsById(form.playerId())).thenReturn(true);
    when(lobbyRepository.existsById(form.lobbyId())).thenReturn(true);

    ValidationTestUtil.assertPassesValidation(() -> lobbyFormValidator.validate(form, LobbyForm.FormType.JOIN_LOBBY));
  }

}
