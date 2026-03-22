import { apiClient } from "@/shared/api/client";
import type { BaseResponse } from "@/shared/types/api";
import type {
  RegisterHubRequest,
  RegisterHubResponse,
  GetHubsResponse,
  DeleteHubResponse,
  ChangeHubAliasRequest,
  ChangeHubAliasResponse,
  SendHubCommandRequest,
  SendHubCommandResponse,
} from "../types";

export async function registerHub(req: RegisterHubRequest) {
  const { data } = await apiClient.post<BaseResponse<RegisterHubResponse>>(
    "/hubs",
    req
  );
  return data.data;
}

export async function getHubs(page = 0, size = 10) {
  const { data } = await apiClient.get<BaseResponse<GetHubsResponse>>("/hubs", {
    params: { page, size },
  });
  return data.data;
}

export async function deleteHub(hubId: number) {
  const { data } = await apiClient.delete<BaseResponse<DeleteHubResponse>>(
    `/hubs/${hubId}`
  );
  return data.data;
}

export async function changeHubAlias(hubId: number, req: ChangeHubAliasRequest) {
  const { data } = await apiClient.put<BaseResponse<ChangeHubAliasResponse>>(
    `/hubs/${hubId}`,
    req
  );
  return data.data;
}

export async function sendHubCommand(hubId: number, req: SendHubCommandRequest) {
  const { data } = await apiClient.post<BaseResponse<SendHubCommandResponse>>(
    `/hubs/${hubId}/command`,
    req
  );
  return data.data;
}

export function connectHubSSE(hubId: number, token: string) {
  const baseUrl =
    process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";
  const eventSource = new EventSource(
    `${baseUrl}/hubs/${hubId}/sse?token=${token}`
  );
  return eventSource;
}
