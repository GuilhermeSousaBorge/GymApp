"use client"
import { Permission } from "@/types/role";
import { User } from "@/types/user";
import { create } from "zustand";
import { persist } from "zustand/middleware";

type Store = {
  user: User | null;
  token: string | null;
  login: (user: User, token: string) => void;
  logout: () => void;
};

export const useAuth = create<Store>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      login: (user, token) => {
        document.cookie = `token=${token}; path=/; max-age=${60 * 60 * 24}`
        set({ user, token })
      },
      logout: () => {
        document.cookie = "token=; path=/; max-age=0"
        set({ user: null, token: null })
      },
    }),
    { name: "auth" }
  )
);

export const useIsAuthenticated = () => useAuth((state) => !!state.user);
export const useUser = () => useAuth((state) => state.user);
export const useHasPermission = (permission: Permission | Permission[]) =>
  useAuth((state) => {
    const perms = state.user?.role?.permissions ?? [];
    return Array.isArray(permission)
      ? permission.some((p) => perms.includes(p))
      : perms.includes(permission);
  });