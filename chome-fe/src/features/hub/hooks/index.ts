"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  getHubs,
  registerHub,
  deleteHub,
  changeHubAlias,
  sendHubCommand,
  inviteHub,
} from "../api";
import type {
  RegisterHubRequest,
  ChangeHubAliasRequest,
  SendHubCommandRequest,
  InviteHubRequest,
} from "../types";

export function useHubs(page = 0, size = 10) {
  return useQuery({
    queryKey: ["hubs", page, size],
    queryFn: () => getHubs(page, size),
  });
}

export function useRegisterHub() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (req: RegisterHubRequest) => registerHub(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["hubs"] }),
  });
}

export function useDeleteHub() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (hubId: number) => deleteHub(hubId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["hubs"] }),
  });
}

export function useChangeHubAlias() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ hubId, req }: { hubId: number; req: ChangeHubAliasRequest }) =>
      changeHubAlias(hubId, req),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["hubs"] }),
  });
}

export function useSendHubCommand(hubId: number) {
  return useMutation({
    mutationFn: (req: SendHubCommandRequest) => sendHubCommand(hubId, req),
  });
}

export function useInviteHub() {
  return useMutation({
    mutationFn: ({ hubId, req }: { hubId: number; req: InviteHubRequest }) =>
      inviteHub(hubId, req),
  });
}

