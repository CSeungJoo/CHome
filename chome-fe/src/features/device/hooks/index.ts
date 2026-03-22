"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getDevices, getDeviceDetail, changeDeviceAlias } from "../api";
import type { ChangeDeviceAliasRequest } from "../types";

export function useDevices(hubId: number) {
  return useQuery({
    queryKey: ["devices", hubId],
    queryFn: () => getDevices(hubId),
    enabled: !!hubId,
  });
}

export function useDeviceDetail(deviceId: number) {
  return useQuery({
    queryKey: ["device", deviceId],
    queryFn: () => getDeviceDetail(deviceId),
    enabled: !!deviceId,
  });
}

export function useChangeDeviceAlias() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      deviceId,
      req,
    }: {
      deviceId: number;
      req: ChangeDeviceAliasRequest;
    }) => changeDeviceAlias(deviceId, req),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ["device", variables.deviceId] });
      qc.invalidateQueries({ queryKey: ["devices"] });
    },
  });
}
