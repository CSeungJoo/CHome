import { apiClient } from "@/shared/api/client";
import type { BaseResponse } from "@/shared/types/api";
import type {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from "../types";

export async function login(req: LoginRequest) {
  const { data } = await apiClient.post<BaseResponse<LoginResponse>>(
    "/auth/login",
    req
  );
  return data.data;
}

export async function register(req: RegisterRequest) {
  const { data } = await apiClient.post<BaseResponse<RegisterResponse>>(
    "/users",
    req
  );
  return data.data;
}

export async function verifyEmail(token: string) {
  const { data } = await apiClient.get<
    BaseResponse<{ message: string }>
  >("/users/verify", { params: { token } });
  return data.data;
}
