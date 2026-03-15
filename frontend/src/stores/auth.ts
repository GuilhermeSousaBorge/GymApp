"use client"
import { Permission } from "@/types/role";
import { User } from "@/types/user";
import { create } from "zustand";

type Store = {
  user: User | null;
  login: (user: User) => void;
  logout: () => void;
};

export const useAuth = create<Store>()((set) => ({
  user: null,
  login: (user) => set({ user }),
  logout: () => {
    set({ user: null });
  },
}));

//#region Selectors
export const useIsAuthenticated = () => useAuth((state) => !!state.user);

export const useUser = () => useAuth((state) => state.user);

export const useHasPermission = (permission: Permission | Permission[]) =>
  useAuth((state) => {
    const perms = state.user?.role?.permissions ?? [];
    return Array.isArray(permission)
      ? permission.some((p) => perms.includes(p))
      : perms.includes(permission);
  });
//#endregion
