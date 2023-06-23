package io.fingersonbuzzers.backend.validation;

import java.util.Map;

public class ValidationFailedException extends Exception {

  private final Map<String, String> validationErrors;

  public ValidationFailedException(Map<String, String> validationErrors) {
    this.validationErrors = validationErrors;
  }

  public Map<String, String> getValidationErrors() {
    return validationErrors;
  }
}
