package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.PlayerRepository;
import io.fingersonbuzzers.backend.validation.FieldError;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyFormValidatorTest {

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private LobbyFormValidator lobbyFormValidator;

  @Test
  void validate_ValidForm_AssertNoErrors() {
    var form = new LobbyForm("test name", null);

    var result = lobbyFormValidator.validate(form);

    assertFalse(result.hasErrors());
    assertThat(result.fieldErrors()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = "        ")
  @NullAndEmptySource
  void validate_MissingPlayerName_AssertError(String blankPlayerName) {
    var form = new LobbyForm(blankPlayerName, null);

    var result = lobbyFormValidator.validate(form);

    assertTrue(result.hasErrors());
    assertThat(result.fieldErrors())
        .extracting(FieldError::fieldName, FieldError::errorCode)
        .containsExactly(tuple(LobbyForm.PLAYER_NAME_FIELD, "required"));
  }

  @Test
  void validate_InvalidPlayerId_AssertError() {
    var form = new LobbyForm("test name", UUID.randomUUID());

    when(playerRepository.existsById(form.playerId())).thenReturn(false);

    var result = lobbyFormValidator.validate(form);

    assertTrue(result.hasErrors());
    assertThat(result.fieldErrors())
        .extracting(FieldError::fieldName, FieldError::errorCode)
        .containsExactly(tuple(LobbyForm.PLAYER_ID_FIELD, "invalid"));
  }

  @Test
  void validate_ValidPlayerId_AssertNoErrors() {
    var form = new LobbyForm("test name", UUID.randomUUID());

    when(playerRepository.existsById(form.playerId())).thenReturn(true);

    var result = lobbyFormValidator.validate(form);

    assertFalse(result.hasErrors());
    assertThat(result.fieldErrors()).isEmpty();
  }

}
