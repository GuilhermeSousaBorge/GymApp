"use client"

import { authService } from "@/services/auth";
import { useAuth } from "@/stores/auth";
import { useEffect, useState } from "react";


export default function AuthProvider({children}: {children: React.ReactNode}) {
  const login = useAuth((s) => s.login);
  const logout = useAuth((s) => s.logout);
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = useAuth.getState().token;
    if(!token){
      logout();
      setIsLoading(false)
      return
    }
    authService
      .me()
      .then((user) => login(user, token))
      .catch(() => {
        // O interceptor do axios ja tenta refresh automaticamente antes de cair aqui.
        // Se chegou no catch, o refresh tambem falhou -> logout.
        logout()
      })
      .finally(() => setIsLoading(false));
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if(isLoading) return null
  return <>{children}</>;
}
