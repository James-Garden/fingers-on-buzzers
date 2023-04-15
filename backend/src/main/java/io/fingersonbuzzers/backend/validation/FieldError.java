package io.fingersonbuzzers.backend.validation;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldError(String fieldName,
                         String errorCode) {
}
