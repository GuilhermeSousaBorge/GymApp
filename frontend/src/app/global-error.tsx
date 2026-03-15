"use client"

import { AlertTriangle } from "lucide-react"
import { useEffect } from "react"

type Props = {
  error: Error & { digest?: string }
  reset: () => void
}

export default function GlobalError({ error, reset }: Props) {
  useEffect(() => {
    console.error(error)
  }, [error])

  return (
    <html lang="pt-BR">
      <body className="min-h-screen bg-zinc-50 flex items-center justify-center p-6 font-sans">
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
            <h1 className="text-2xl font-bold text-zinc-900">Erro crítico</h1>
            <p className="text-zinc-500 text-sm">
              A aplicação encontrou um erro grave. Tente recarregar a página.
            </p>
            {error.digest && (
              <p className="text-xs text-zinc-400 font-mono mt-1">
                ID: {error.digest}
              </p>
            )}
          </div>

          <div className="flex gap-3">
            <button
              onClick={reset}
              className="px-4 py-2 rounded-md bg-yellow-400 text-zinc-900 font-medium hover:bg-yellow-500 transition-colors"
            >
              Tentar novamente
            </button>
            <a
              href="/dashboard"
              className="px-4 py-2 rounded-md border border-zinc-200 text-zinc-700 font-medium hover:bg-zinc-100 transition-colors"
            >
              Ir ao início
            </a>
          </div>

        </div>
      </body>
    </html>
  )
}