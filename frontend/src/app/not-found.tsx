import { Button } from "@/components/ui/button"
import { FileQuestion } from "lucide-react"
import Link from "next/link"

export default function NotFound() {
  return (
    <div className="min-h-screen bg-zinc-50 flex items-center justify-center p-6">
      <div className="flex flex-col items-center text-center max-w-md gap-6">

        <div className="relative">
          <span className="text-[120px] font-black text-zinc-100 leading-none select-none">
            404
          </span>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="h-16 w-16 rounded-full bg-yellow-400 flex items-center justify-center shadow-lg">
              <FileQuestion className="h-8 w-8 text-zinc-900" />
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <h1 className="text-2xl font-bold text-zinc-900">Página não encontrada</h1>
          <p className="text-zinc-500 text-sm">
            A página que você está procurando não existe ou foi movida.
          </p>
        </div>

        <div className="flex gap-3">
          <Button asChild variant="outline">
            <Link href="javascript:history.back()">Voltar</Link>
          </Button>
          <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
            <Link href="/dashboard">Ir ao início</Link>
          </Button>
        </div>

      </div>
    </div>
  )
}