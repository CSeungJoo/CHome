import { apiClient } from "@/shared/api/client";
import type { BaseResponse } from "@/shared/types/api";
import type {
  GetDevicesResponse,
  DeviceDetail,
  ChangeDeviceAliasRequest,
  ChangeDeviceAliasResponse,
} from "../types";

export async function getDevices(hubId: number) {
  const { data } = await apiClient.get<BaseResponse<GetDevicesResponse>>(
    "/devices",
    { params: { hubId } }
  );
  return data.data;
}

export async function getDeviceDetail(deviceId: number) {
  const { data } = await apiClient.get<BaseResponse<DeviceDetail>>(
    `/devices/${deviceId}`
  );
  return data.data;
}

export async function changeDeviceAlias(
  deviceId: number,
  req: ChangeDeviceAliasRequest
) {
  const { data } = await apiClient.put<BaseResponse<ChangeDeviceAliasResponse>>(
    `/devices/${deviceId}`,
    req
  );
  return data.data;
}
