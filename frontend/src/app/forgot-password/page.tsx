"use client"

import { AuthCard } from "@/components/auth/auth-card"
import { AuthHeader } from "@/components/auth/auth-header"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { forgotPasswordSchema } from "@/lib/validations/auth"
import { authService } from "@/services/auth"
import { handleMessageError } from "@/lib/handle-error"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { toast } from "sonner"
import { useState } from "react"
import { ArrowLeft, Mail } from "lucide-react"
import Link from "next/link"
import z from "zod"

type ForgotPasswordFormData = z.infer<typeof forgotPasswordSchema>

export default function ForgotPasswordPage() {
    const [submitted, setSubmitted] = useState(false)

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<ForgotPasswordFormData>({
        resolver: zodResolver(forgotPasswordSchema),
    })

    const onSubmit = async (data: ForgotPasswordFormData) => {
        try {
            await authService.forgotPassword(data.email)
            setSubmitted(true)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="min-h-screen bg-zinc-100 flex items-center justify-center p-6">
            <AuthCard>
                {!submitted ? (
                    <>
                        <AuthHeader
                            title="Esqueceu a senha?"
                            description="Informe seu email para receber um link de redefinição"
                        />
                        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 px-6 pb-6">
                            <div className="space-y-2">
                                <Label>Email</Label>
                                <Input
                                    type="email"
                                    placeholder="seu@email.com"
                                    {...register("email")}
                                />
                                {errors.email && (
                                    <p className="text-sm text-red-500">{errors.email.message}</p>
                                )}
                            </div>

                            <Button
                                disabled={isSubmitting}
                                className="w-full h-11 bg-yellow-400 text-zinc-900 hover:bg-yellow-500"
                            >
                                Enviar link de redefinição
                            </Button>

                            <div className="flex justify-center">
                                <Button asChild variant="link" className="text-sm text-muted-foreground hover:text-yellow-500">
                                    <Link href="/auth">
                                        <ArrowLeft className="h-4 w-4 mr-1" />
                                        Voltar para o login
                                    </Link>
                                </Button>
                            </div>
                        </form>
                    </>
                ) : (
                    <div className="flex flex-col items-center gap-4 py-10 px-6">
                        <div className="h-16 w-16 rounded-full bg-yellow-100 flex items-center justify-center">
                            <Mail className="h-8 w-8 text-yellow-600" />
                        </div>
                        <AuthHeader
                            title="Email enviado"
                            description="Se o email estiver cadastrado, você receberá um link para redefinir sua senha. Verifique sua caixa de entrada."
                        />
                        <Button asChild className="bg-yellow-400 text-zinc-900 hover:bg-yellow-500">
                            <Link href="/auth">Voltar para o login</Link>
                        </Button>
                    </div>
                )}
            </AuthCard>
        </div>
    )
}
