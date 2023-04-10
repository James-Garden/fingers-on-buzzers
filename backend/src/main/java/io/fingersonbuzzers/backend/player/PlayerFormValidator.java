package io.fingersonbuzzers.backend.player;

import io.fingersonbuzzers.backend.validation.ValidationResult;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class PlayerFormValidator {

  public ValidationResult validate(PlayerForm form) {
    var errors = new ValidationResult();

    if (StringUtils.isBlank(form.playerName())) {
      errors.reject("playerName", "required");
    }

    return errors;
  }
}
