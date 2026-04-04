"use client"

import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"

interface BackButtonProps {
  variant?: "outline" | "default" | "destructive" | "secondary" | "ghost" | "link"
  className?: string
  children?: React.ReactNode
}

export function BackButton({ variant = "outline", className, children = "Voltar" }: BackButtonProps) {
  const router = useRouter()

  return (
    <Button variant={variant} className={className} onClick={() => router.back()}>
      {children}
    </Button>
  )
}
