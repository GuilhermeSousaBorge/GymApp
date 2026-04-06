"use client"

import { AuthCard } from "@/components/auth/auth-card"
import { AuthHeader } from "@/components/auth/auth-header"
import { authService } from "@/services/auth"
import { useSearchParams } from "next/navigation"
import { useEffect, useState } from "react"
import { CheckCircle, XCircle, Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import Link from "next/link"

export default function VerifyEmailPage() {
    const searchParams = useSearchParams()
    const token = searchParams.get("token")

    const [status, setStatus] = useState<"loading" | "success" | "error">("loading")
    const [message, setMessage] = useState("")

    useEffect(() => {
        if (!token) {
            setStatus("error")
            setMessage("Token de verificação não encontrado.")
            return
        }

        authService
            .confirmEmailVerification(token)
            .then((res) => {
                setStatus("success")
                setMessage(res.message || "Email verificado com sucesso!")
            })
            .catch(() => {
                setStatus("error")
                setMessage("Token inválido ou expirado. Solicite uma nova verificação.")
            })
    }, [token])

    return (
        <div className="min-h-screen bg-zinc-100 flex items-center justify-center p-6">
            <AuthCard>
                {status === "loading" && (
                    <div className="flex flex-col items-center gap-4 py-10">
                        <Loader2 className="h-10 w-10 animate-spin text-yellow-500" />
                        <p className="text-zinc-600">Verificando seu email...</p>
                    </div>
                )}

                {status === "success" && (
                    <div className="flex flex-col items-center gap-4 py-10">
                        <div className="h-16 w-16 rounded-full bg-green-100 flex items-center justify-center">
                            <CheckCircle className="h-8 w-8 text-green-600" />
                        </div>
                        <AuthHeader title="Email verificado" description={message} />
                        <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
                            <Link href="/auth">Ir para o login</Link>
                        </Button>
                    </div>
                )}

                {status === "error" && (
                    <div className="flex flex-col items-center gap-4 py-10">
                        <div className="h-16 w-16 rounded-full bg-red-100 flex items-center justify-center">
                            <XCircle className="h-8 w-8 text-red-600" />
                        </div>
                        <AuthHeader title="Falha na verificação" description={message} />
                        <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
                            <Link href="/auth">Voltar para o login</Link>
                        </Button>
                    </div>
                )}
            </AuthCard>
        </div>
    )
}
