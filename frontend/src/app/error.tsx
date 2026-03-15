"use client"

import { Button } from "@/components/ui/button"
import { AlertTriangle } from "lucide-react"
import { useRouter } from "next/navigation"
import { useEffect } from "react"

type Props = {
  error: Error & { digest?: string }
  reset: () => void
}

export default function Error({ error, reset }: Props) {
  const router = useRouter()

  useEffect(() => {
    console.error(error)
  }, [error])

  return (
    <div className="min-h-screen bg-zinc-50 flex items-center justify-center p-6">
      <div className="flex flex-col items-center text-center max-w-md gap-6">

        <div className="relative">
          <span className="text-[120px] font-black text-zinc-100 leading-none select-none">
            500
          </span>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="h-16 w-16 rounded-full bg-red-100 flex items-center justify-center shadow-lg">
              <AlertTriangle className="h-8 w-8 text-red-600" />
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <h1 className="text-2xl font-bold text-zinc-900">Algo deu errado</h1>
          <p className="text-zinc-500 text-sm">
            Ocorreu um erro inesperado. Tente novamente ou volte ao início.
          </p>
          {error.digest && (
            <p className="text-xs text-zinc-400 font-mono mt-1">
              ID: {error.digest}
            </p>
          )}
        </div>

        <div className="flex gap-3">
          <Button variant="outline" onClick={() => router.back()}>
            Voltar
          </Button>
          <Button
            onClick={reset}
            className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500"
          >
            Tentar novamente
          </Button>
          <Button asChild variant="ghost">
            <a href="/dashboard">Ir ao início</a>
          </Button>
        </div>

      </div>
    </div>
  )
}