package io.fingersonbuzzers.backend.validation;

import java.util.HashSet;
import java.util.Set;

public record ValidationResult(Set<FieldError> fieldErrors) {

  public ValidationResult() {
    this(new HashSet<>());
  }

  public void reject(String fieldName, String errorCode) {
    fieldErrors.add(new FieldError(fieldName, errorCode));
  }

  public boolean hasErrors() {
    return !fieldErrors.isEmpty();
  }
}
