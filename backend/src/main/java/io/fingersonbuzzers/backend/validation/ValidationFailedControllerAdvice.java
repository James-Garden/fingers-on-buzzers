package io.fingersonbuzzers.backend.validation;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationFailedControllerAdvice {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidationFailedControllerAdvice.class);

  @ExceptionHandler(ValidationFailedException.class)
  public ResponseEntity<Map<String, String>> handleValidationFailedException(ValidationFailedException exception,
                                                                             HttpServletRequest request) {
    LOGGER.debug(
        "Validation failed for request to '{}' with the following errors: {}",
        request.getServletPath(),
        exception.getValidationErrors()
    );

    return ResponseEntity.unprocessableEntity().body(exception.getValidationErrors());
  }
}
