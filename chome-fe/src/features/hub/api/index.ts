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
  InviteHubRequest,
  InviteHubResponse,
} from "../types";

export async function registerHub(req: RegisterHubRequest) {
  const { data } = await apiClient.post<BaseResponse<RegisterHubResponse>>(
    "/hubs",
    req
  );
  return data.data;
}

export async function inviteHub(hubId: number, req: InviteHubRequest) {
  const { data } = await apiClient.post<BaseResponse<InviteHubResponse>>(
    `/hubs/${hubId}/invite`,
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

export function connectHubSSE(
  hubId: number,
  token: string,
  onMessage: (data: string) => void,
  onOpen: () => void,
  onError: () => void
) {
  const baseUrl =
    process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";
  const controller = new AbortController();

  fetch(`${baseUrl}/hubs/${hubId}/sse`, {
    headers: { Authorization: `Bearer ${token}` },
    signal: controller.signal,
  })
    .then(async (res) => {
      if (!res.ok || !res.body) {
        onError();
        return;
      }
      onOpen();
      const reader = res.body.getReader();
      const decoder = new TextDecoder();
      let buffer = "";

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        buffer += decoder.decode(value, { stream: true });
        const lines = buffer.split("\n");
        buffer = lines.pop() ?? "";
        for (const line of lines) {
          if (line.startsWith("data:")) {
            onMessage(line.slice(5).trim());
          }
        }
      }
      onError();
    })
    .catch(() => onError());

  return controller;
}
