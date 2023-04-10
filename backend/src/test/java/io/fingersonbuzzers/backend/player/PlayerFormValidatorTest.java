package io.fingersonbuzzers.backend.player;

import io.fingersonbuzzers.backend.validation.FieldError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PlayerFormValidatorTest {

  @InjectMocks
  private PlayerFormValidator playerFormValidator;

  @Test
  void validate_ValidPlayerName() {
    var form = new PlayerForm("player name");

    var errors = playerFormValidator.validate(form);

    assertFalse(errors.hasErrors());
    assertThat(errors.fieldErrors()).isEmpty();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = "    ")
  void validate_InvalidPlayerName_AssertErrors(String invalidPlayerName) {
    var form = new PlayerForm(invalidPlayerName);

    var errors = playerFormValidator.validate(form);

    assertTrue(errors.hasErrors());
    assertThat(errors.fieldErrors()).extracting(
        FieldError::fieldName,
        FieldError::errorCode,
        FieldError::additionalData
    ).containsExactly(
        tuple(
            "playerName", "required", null
        )
    );
  }

}
