"use client"

import { authService } from "@/services/auth";
import { useAuth } from "@/stores/auth";
import { useEffect, useState } from "react";


export default function AuthProvider({children}: {children: React.ReactNode}) {
  const login = useAuth((s) => s.login);
  const logout = useAuth((s) => s.logout);
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    authService.me().then(login).catch(logout).finally(() => setIsLoading(false));
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if(isLoading) return null
  return <>{children}</>;
}
