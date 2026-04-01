import { Button } from "@/components/ui/button"
import { BackButton } from "@/components/ui/back-button"
import { ShieldOff } from "lucide-react"
import Link from "next/link"

export default function Unauthorized() {
  return (
    <div className="min-h-screen bg-zinc-50 flex items-center justify-center p-6">
      <div className="flex flex-col items-center text-center max-w-md gap-6">

        <div className="relative">
          <span className="text-[120px] font-black text-zinc-100 leading-none select-none">
            401
          </span>
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="h-16 w-16 rounded-full bg-orange-100 flex items-center justify-center shadow-lg">
              <ShieldOff className="h-8 w-8 text-orange-600" />
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <h1 className="text-2xl font-bold text-zinc-900">Acesso não autorizado</h1>
          <p className="text-zinc-500 text-sm">
            Você não tem permissão para acessar esta página. Faça login ou contate o administrador.
          </p>
        </div>

        <div className="flex gap-3">
          <BackButton />
          <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
            <Link href="/auth">Fazer login</Link>
          </Button>
        </div>

      </div>
    </div>
  )
}