"use client"

import { AuthCard } from "@/components/auth/auth-card"
import { AuthHeader } from "@/components/auth/auth-header"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { resetPasswordSchema } from "@/lib/validations/auth"
import { authService } from "@/services/auth"
import { handleMessageError } from "@/lib/handle-error"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { useSearchParams } from "next/navigation"
import { toast } from "sonner"
import { useState } from "react"
import { CheckCircle, XCircle } from "lucide-react"
import Link from "next/link"
import z from "zod"

type ResetPasswordFormData = z.infer<typeof resetPasswordSchema>

export default function ResetPasswordPage() {
    const searchParams = useSearchParams()
    const token = searchParams.get("token")

    const [status, setStatus] = useState<"form" | "success" | "error">(
        token ? "form" : "error"
    )
    const [errorMessage, setErrorMessage] = useState(
        token ? "" : "Token de redefinição não encontrado."
    )

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<ResetPasswordFormData>({
        resolver: zodResolver(resetPasswordSchema),
    })

    const onSubmit = async (data: ResetPasswordFormData) => {
        if (!token) return
        try {
            await authService.resetPassword(token, data.newPassword)
            setStatus("success")
        } catch (err) {
            const message = handleMessageError(err)
            setStatus("error")
            setErrorMessage(message)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="min-h-screen bg-zinc-100 flex items-center justify-center p-6">
            <AuthCard>
                {status === "form" && (
                    <>
                        <AuthHeader
                            title="Redefinir senha"
                            description="Escolha uma nova senha para sua conta"
                        />
                        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 px-6 pb-6">
                            <div className="space-y-2">
                                <Label>Nova senha</Label>
                                <Input
                                    type="password"
                                    placeholder="Mínimo 8 caracteres"
                                    {...register("newPassword")}
                                />
                                {errors.newPassword && (
                                    <p className="text-sm text-red-500">{errors.newPassword.message}</p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label>Confirmar nova senha</Label>
                                <Input
                                    type="password"
                                    {...register("confirmPassword")}
                                />
                                {errors.confirmPassword && (
                                    <p className="text-sm text-red-500">{errors.confirmPassword.message}</p>
                                )}
                            </div>

                            <Button
                                disabled={isSubmitting}
                                className="w-full h-11 bg-yellow-400 text-zinc-900 hover:bg-yellow-500"
                            >
                                Redefinir senha
                            </Button>
                        </form>
                    </>
                )}

                {status === "success" && (
                    <div className="flex flex-col items-center gap-4 py-10">
                        <div className="h-16 w-16 rounded-full bg-green-100 flex items-center justify-center">
                            <CheckCircle className="h-8 w-8 text-green-600" />
                        </div>
                        <AuthHeader
                            title="Senha redefinida"
                            description="Sua senha foi alterada com sucesso. Faça login com a nova senha."
                        />
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
                        <AuthHeader
                            title="Falha na redefinição"
                            description={errorMessage || "Token inválido ou expirado. Solicite uma nova redefinição."}
                        />
                        <div className="flex gap-3">
                            <Button asChild variant="outline">
                                <Link href="/forgot-password">Solicitar novo link</Link>
                            </Button>
                            <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
                                <Link href="/auth">Voltar para o login</Link>
                            </Button>
                        </div>
                    </div>
                )}
            </AuthCard>
        </div>
    )
}
