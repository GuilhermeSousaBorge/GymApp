import { create } from "zustand";

type Store = {
  sidebarOpen: boolean;
  loading: boolean;
  toggleSidebar: () => void;
  setLoading: (value: boolean) => void;
  setSideBarOpen: (value: boolean) => void
};

export const useUi = create<Store>()((set) => ({
  sidebarOpen: true,
  loading: false,
  toggleSidebar: () => {
    set((state) => ({ sidebarOpen: !state.sidebarOpen }));
  },
  setLoading: (value: boolean) => {
    set({loading: value})
  },
  setSideBarOpen: (value: boolean) => {
    set({sidebarOpen: value})
  }
}));
