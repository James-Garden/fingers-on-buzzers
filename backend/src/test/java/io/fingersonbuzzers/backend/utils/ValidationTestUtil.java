package io.fingersonbuzzers.backend.utils;

import io.fingersonbuzzers.backend.validation.ValidationFailedException;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class ValidationTestUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ValidationTestUtil() {}

  public static Map<String, String> failedValidationResult() {
    return Map.of("testField", "testError");
  }

  public static String failedValidationResultJson() {
    try {
      return MAPPER.writeValueAsString(failedValidationResult());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to construct JSON string for test, did the jackson API change?", e);
    }
  }

  public static void assertPassesValidation(ThrowableAssert.ThrowingCallable throwingCallable) {
    assertThatNoException()
        .isThrownBy(throwingCallable);
  }

  public static void assertFailsValidationWithErrors(ThrowableAssert.ThrowingCallable throwingCallable,
                                                     Map<String, String> expectedValidationErrors) {
    assertThatExceptionOfType(ValidationFailedException.class)
        .isThrownBy(throwingCallable)
        .extracting(ValidationFailedException::getValidationErrors)
        .isEqualTo(expectedValidationErrors);
  }
}
