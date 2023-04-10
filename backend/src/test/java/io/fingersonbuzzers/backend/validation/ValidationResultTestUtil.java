package io.fingersonbuzzers.backend.validation;

import java.util.Collections;

public class ValidationResultTestUtil {

  private ValidationResultTestUtil() {}

  public static ValidationResult validationResultWithError() {
    return new ValidationResult(Collections.singleton(new FieldError("testField", "testError")));
  }

  public static ValidationResult validationResultNoError() {
    return new ValidationResult(Collections.emptySet());
  }
}
