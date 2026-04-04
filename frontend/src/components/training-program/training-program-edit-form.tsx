"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import { Textarea } from "@/components/ui/textarea"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { EditPageHeader } from "@/components/ui/edit-page-header"
import { useCreateProgram, useProgramDetails, useUpdateProgram } from "@/hooks/training-program"
import { useUsers } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { TrainingProgramFormData, trainingProgramSchema } from "@/lib/validations/training-program"
import { useUser } from "@/stores/auth"
import { zodResolver } from "@hookform/resolvers/zod"
import { ClipboardList } from "lucide-react"
import { useRouter } from "next/navigation"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface TrainingProgramEditFormProps {
    programId?: number
}

export function TrainingProgramEditForm({ programId }: TrainingProgramEditFormProps) {
    const router = useRouter()
    const isEditing = !!programId

    const { data: program, isLoading, error } = useProgramDetails(programId)
    const { data: users = [] } = useUsers()
    const me = useUser()
    const isAdminOrTrainer = me?.role?.name === "Administrador" || me?.role?.name === "PersonalTrainer"

    const { mutateAsync: createProgram } = useCreateProgram()
    const { mutateAsync: updateProgram } = useUpdateProgram()

    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting },
    } = useForm<TrainingProgramFormData>({
        resolver: zodResolver(trainingProgramSchema),
        values: {
            active: program?.active ?? true,
            description: program?.description ?? "",
            name: program?.name ?? "",
            userId: program?.userId ?? me?.id ?? 0,
            trainerId: program?.trainerId,
        },
    })

    const onSubmit = async (data: TrainingProgramFormData) => {
        try {
            if (isEditing) {
                await updateProgram({ id: programId!, program: data })
                toast.success("Programa editado com sucesso", { position: "top-right" })
            } else {
                await createProgram(data)
                toast.success("Programa criado com sucesso", { position: "top-right" })
            }
            router.push("/training-programs")
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState message="Carregando programa..." />
    if (isEditing && error) return <ErrorState message="Erro ao carregar programa" />

    return (
        <div className="flex flex-col gap-6">
            <EditPageHeader
                icon={ClipboardList}
                title={isEditing ? "Editar programa" : "Novo programa"}
                subtitle={isEditing ? "Atualize as informações do programa" : "Preencha as informações do novo programa"}
                onBack={() => router.push("/training-programs")}
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
                                <Input {...register("name")} placeholder="Nome do programa" />
                                <p className="text-sm text-red-500">{errors.name?.message}</p>
                            </Field>

                            <Field>
                                <Label>Descrição</Label>
                                <Textarea {...register("description")} placeholder="Descreva o objetivo do programa..." />
                                <p className="text-sm text-red-500">{errors.description?.message}</p>
                            </Field>

                            <Field>
                                <Label>Aluno</Label>
                                {isAdminOrTrainer ? (
                                    <Controller
                                        control={control}
                                        name="userId"
                                        render={({ field }) => {
                                            const userExists = users.some((c) => String(c.id) === String(field.value))
                                            const safeValue = field.value && userExists ? String(field.value) : ""
                                            return (
                                                <Select
                                                    value={safeValue}
                                                    onValueChange={(val) => {
                                                        const num = Number(val)
                                                        if (num > 0) field.onChange(num)
                                                    }}
                                                >
                                                    <SelectTrigger>
                                                        <SelectValue placeholder="Selecione o aluno" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        {users.map((u) => (
                                                            <SelectItem key={u.id} value={String(u.id)}>
                                                                {u.name}
                                                            </SelectItem>
                                                        ))}
                                                    </SelectContent>
                                                </Select>
                                            )
                                        }}
                                    />
                                ) : (
                                    <Input value={me?.name ?? ""} disabled />
                                )}
                                <p className="text-sm text-red-500">{errors.userId?.message}</p>
                            </Field>

                            <Field>
                                <Label>Instrutor / Personal <span className="text-muted-foreground text-xs">(opcional)</span></Label>
                                <Controller
                                    control={control}
                                    name="trainerId"
                                    render={({ field }) => {
                                        const userExists = users.some((c) => String(c.id) === String(field.value))
                                        const safeValue = field.value && userExists ? String(field.value) : ""
                                        return (
                                            <Select
                                                value={safeValue}
                                                onValueChange={(val) => {
                                                    const num = Number(val)
                                                    if (num > 0) field.onChange(num)
                                                }}
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Selecione o instrutor" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {users.map((u) => (
                                                        <SelectItem key={u.id} value={String(u.id)}>
                                                            {u.name}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        )
                                    }}
                                />
                            </Field>

                            <Field>
                                <Label>Ativo</Label>
                                <Controller
                                    control={control}
                                    name="active"
                                    render={({ field }) => (
                                        <div className="flex items-center gap-3">
                                            <Switch checked={field.value} onCheckedChange={field.onChange} />
                                        </div>
                                    )}
                                />
                                <p className="text-sm text-red-500">{errors.active?.message}</p>
                            </Field>
                        </FieldGroup>
                    </CardContent>
                </Card>

                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={() => router.push("/training-programs")}>
                        Cancelar
                    </Button>
                    <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                        {isEditing ? "Salvar alterações" : "Criar programa"}
                    </Button>
                </div>
            </form>
        </div>
    )
}
