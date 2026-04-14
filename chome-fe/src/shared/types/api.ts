export interface BaseResponse<T> {
  status: "SUCCESS" | "ERROR";
  data: T;
}

export interface BaseError {
  message: string;
  code: string;
}

export interface MsgResponse {
  message: string;
}

export interface PaginatedRequest {
  page?: number;
  size?: number;
}
