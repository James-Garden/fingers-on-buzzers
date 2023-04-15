export interface ValidationResult {
  fieldErrors: [FieldError]
}

export interface FieldError {
  fieldName: string,
  errorCode: string
}
