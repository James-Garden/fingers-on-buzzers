import type { AxiosResponse } from "axios";
import { HttpStatusCode } from "axios";

export function hasValidationErrors(response: AxiosResponse<any>): boolean {
  return response.status === HttpStatusCode.UnprocessableEntity;
}
