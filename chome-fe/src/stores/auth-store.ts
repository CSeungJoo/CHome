import { create } from "zustand";

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userId: number | null;
  isAuthenticated: boolean;
  setAuth: (accessToken: string, refreshToken: string, userId: number) => void;
  clearAuth: () => void;
  hydrate: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  userId: null,
  isAuthenticated: false,

  setAuth: (accessToken, refreshToken, userId) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
    localStorage.setItem("userId", String(userId));
    set({ accessToken, refreshToken, userId, isAuthenticated: true });
  },

  clearAuth: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("userId");
    set({
      accessToken: null,
      refreshToken: null,
      userId: null,
      isAuthenticated: false,
    });
  },

  hydrate: () => {
    if (typeof window === "undefined") return;
    const accessToken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");
    const userId = localStorage.getItem("userId");
    if (accessToken && refreshToken && userId) {
      set({
        accessToken,
        refreshToken,
        userId: Number(userId),
        isAuthenticated: true,
      });
    }
  },
}));
