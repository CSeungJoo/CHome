"use client";

import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/auth-store";
import { login, register } from "../api";
import type { LoginRequest, RegisterRequest } from "../types";

export function useLogin() {
  const router = useRouter();
  const setAuth = useAuthStore((s) => s.setAuth);

  return useMutation({
    mutationFn: (req: LoginRequest) => login(req),
    onSuccess: (data) => {
      setAuth(data.accessToken, data.refreshToken, data.userId);
      router.push("/dashboard");
    },
  });
}

export function useRegister() {
  const router = useRouter();

  return useMutation({
    mutationFn: (req: RegisterRequest) => register(req),
    onSuccess: () => {
      router.push("/login?registered=true");
    },
  });
}
