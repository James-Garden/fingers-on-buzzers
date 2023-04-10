package io.fingersonbuzzers.backend.validation;

import java.util.HashSet;
import java.util.Set;

public record ValidationResult(Set<FieldError> fieldErrors) {

  public ValidationResult() {
    this(new HashSet<>());
  }

  public void reject(String fieldName, String errorCode) {
    reject(fieldName, errorCode, null);
  }

  public void reject(String fieldName, String errorCode, Object additionalData) {
    fieldErrors.add(new FieldError(fieldName, errorCode, additionalData));
  }

  public boolean hasErrors() {
    return !fieldErrors.isEmpty();
  }
}
