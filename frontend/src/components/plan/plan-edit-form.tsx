"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Textarea } from "@/components/ui/textarea"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { EditPageHeader } from "@/components/ui/edit-page-header"
import { useCreatePlan, usePlanDetails, useUpdatePlan } from "@/hooks/plan"
import { handleMessageError } from "@/lib/handle-error"
import { isAdmin } from "@/lib/utils"
import { PlanFormData, planSchema } from "@/lib/validations/plan"
import { useUser } from "@/stores/auth"
import { zodResolver } from "@hookform/resolvers/zod"
import { Crown, Plus, X } from "lucide-react"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface PlanEditFormProps {
    planId?: number
}

export function PlanEditForm({ planId }: PlanEditFormProps) {
    const router = useRouter()
    const isEditing = !!planId
    const user = useUser()
    const isAdminUser = isAdmin(user)

    const { data: plan, isLoading, error } = usePlanDetails(planId)
    const { mutateAsync: updatePlan } = useUpdatePlan()
    const { mutateAsync: createPlan } = useCreatePlan()

    const [benefitInput, setBenefitInput] = useState("")

    const {
        register,
        handleSubmit,
        control,
        watch,
        setValue,
        formState: { errors, isSubmitting },
    } = useForm<PlanFormData>({
        resolver: zodResolver(planSchema),
        values: {
            name: plan?.name ?? "",
            description: plan?.description ?? "",
            price: plan?.price ?? 0,
            maxStudents: plan?.maxStudents ?? 0,
            maxPrograms: plan?.maxPrograms ?? 1,
            benefits: plan?.benefits ?? [],
            active: plan?.active ?? true,
        },
    })

    const benefits = watch("benefits") ?? []

    const addBenefit = () => {
        const trimmed = benefitInput.trim()
        if (trimmed && !benefits.includes(trimmed)) {
            setValue("benefits", [...benefits, trimmed])
            setBenefitInput("")
        }
    }

    const removeBenefit = (index: number) => {
        setValue("benefits", benefits.filter((_, i) => i !== index))
    }

    const onSubmit = async (data: PlanFormData) => {
        try {
            if (isEditing) {
                await updatePlan({ id: planId!, data })
                toast.success("Plano atualizado com sucesso!")
            } else {
                await createPlan(data)
                toast.success("Plano criado com sucesso!")
            }
            router.push("/plans")
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState message="Carregando plano..." />
    if (isEditing && error) return <ErrorState message="Erro ao carregar plano" />

    if (!isAdminUser && !isEditing) {
        router.push("/plans")
        return null
    }

    const readOnly = !isAdminUser

    return (
        <div className="flex flex-col gap-6">
            <EditPageHeader
                icon={Crown}
                title={readOnly ? "Detalhes do plano" : isEditing ? "Editar plano" : "Novo plano"}
                subtitle={
                    readOnly
                        ? "Visualize as informações do plano"
                        : isEditing
                            ? "Atualize as informações do plano"
                            : "Preencha as informações do novo plano"
                }
                onBack={() => router.push("/plans")}
            />

            <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Informações gerais</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <FieldGroup>
                            <Field>
                                <Label>Nome</Label>
                                <Input {...register("name")} disabled={readOnly} />
                                <p className="text-sm text-red-500">{errors.name?.message}</p>
                            </Field>

                            <Field>
                                <Label>Descrição</Label>
                                <Textarea {...register("description")} placeholder="Descreva o plano..." disabled={readOnly} />
                                <p className="text-sm text-red-500">{errors.description?.message}</p>
                            </Field>

                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <Field>
                                    <Label>Preço (R$)</Label>
                                    <Input type="number" step="0.01" min="0" {...register("price", { valueAsNumber: true })} disabled={readOnly} />
                                    <p className="text-sm text-red-500">{errors.price?.message}</p>
                                </Field>

                                <Field>
                                    <Label>Max. Alunos</Label>
                                    <Input type="number" min="0" {...register("maxStudents", { valueAsNumber: true })} disabled={readOnly} />
                                    <p className="text-sm text-red-500">{errors.maxStudents?.message}</p>
                                </Field>

                                <Field>
                                    <Label>Max. Programas</Label>
                                    <Input type="number" min="1" {...register("maxPrograms", { valueAsNumber: true })} disabled={readOnly} />
                                    <p className="text-sm text-red-500">{errors.maxPrograms?.message}</p>
                                </Field>
                            </div>

                            {isEditing && isAdminUser && (
                                <Field>
                                    <Label>Ativo</Label>
                                    <Controller
                                        control={control}
                                        name="active"
                                        render={({ field }) => (
                                            <div className="flex items-center gap-3">
                                                <Switch checked={field.value} onCheckedChange={field.onChange} />
                                                <span className="text-sm text-muted-foreground">
                                                    {field.value ? "Ativo" : "Inativo"}
                                                </span>
                                            </div>
                                        )}
                                    />
                                </Field>
                            )}
                        </FieldGroup>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Benefícios</CardTitle>
                    </CardHeader>
                    <CardContent>
                        {!readOnly && (
                            <div className="flex gap-2 mb-4">
                                <Input
                                    placeholder="Adicionar benefício..."
                                    value={benefitInput}
                                    onChange={(e) => setBenefitInput(e.target.value)}
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter") {
                                            e.preventDefault()
                                            addBenefit()
                                        }
                                    }}
                                />
                                <Button type="button" variant="outline" onClick={addBenefit}>
                                    <Plus className="h-4 w-4" />
                                </Button>
                            </div>
                        )}
                        {benefits.length === 0 ? (
                            <p className="text-sm text-muted-foreground">Nenhum benefício adicionado</p>
                        ) : (
                            <div className="flex flex-wrap gap-2">
                                {benefits.map((benefit, index) => (
                                    <span
                                        key={index}
                                        className="inline-flex items-center gap-1 rounded-md bg-primary/10 px-3 py-1 text-sm text-primary"
                                    >
                                        {benefit}
                                        {!readOnly && (
                                            <button
                                                type="button"
                                                onClick={() => removeBenefit(index)}
                                                className="ml-1 hover:text-destructive"
                                            >
                                                <X className="h-3 w-3" />
                                            </button>
                                        )}
                                    </span>
                                ))}
                            </div>
                        )}
                    </CardContent>
                </Card>

                {isAdminUser && (
                    <div className="flex justify-end gap-2">
                        <Button type="button" variant="outline" onClick={() => router.push("/plans")}>
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                            {isEditing ? "Salvar alterações" : "Criar plano"}
                        </Button>
                    </div>
                )}
            </form>
        </div>
    )
}
