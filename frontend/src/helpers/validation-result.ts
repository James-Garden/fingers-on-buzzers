export interface ValidationResult {
  fieldErrors: [FieldError]
}

export interface FieldError {
  fieldName: string,
  errorCode: string
}

export function findFieldError(validationResult: ValidationResult|undefined,
                               fieldName: string) {
  return validationResult?.fieldErrors.find(
    fieldError => fieldError.fieldName === fieldName
  );
}
