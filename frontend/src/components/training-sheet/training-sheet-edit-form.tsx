"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Checkbox } from "@/components/ui/checkbox"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Switch } from "@/components/ui/switch"
import { Textarea } from "@/components/ui/textarea"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { EditPageHeader } from "@/components/ui/edit-page-header"
import { useCreateSheet, useSheetDetails, useUpdateSheet } from "@/hooks/training-sheet"
import { handleMessageError } from "@/lib/handle-error"
import { TrainingSheetFormData, trainingSheetSchema } from "@/lib/validations/training-sheet"
import { weekDayOptions } from "@/types/training"
import { zodResolver } from "@hookform/resolvers/zod"
import { CalendarDays } from "lucide-react"
import { useRouter } from "next/navigation"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface TrainingSheetEditFormProps {
    programId: number
    sheetId?: number
}

export function TrainingSheetEditForm({ programId, sheetId }: TrainingSheetEditFormProps) {
    const router = useRouter()
    const isEditing = !!sheetId

    const { data: sheet, isLoading, error } = useSheetDetails(sheetId)
    const { mutateAsync: createSheet } = useCreateSheet()
    const { mutateAsync: updateSheet } = useUpdateSheet()

    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting },
    } = useForm<TrainingSheetFormData>({
        resolver: zodResolver(trainingSheetSchema),
        values: {
            active: sheet?.active ?? true,
            description: sheet?.description ?? "",
            name: sheet?.name ?? "",
            trainingProgramId: sheet?.trainingProgramId ?? programId,
            weekdays: sheet?.weekdays ?? [],
            restTimeSeconds: sheet?.restTimeSeconds,
        },
    })

    const onSubmit = async (data: TrainingSheetFormData) => {
        try {
            if (isEditing) {
                await updateSheet({ id: sheetId!, sheet: data })
                toast.success("Ficha atualizada com sucesso", { position: "top-right" })
            } else {
                await createSheet(data)
                toast.success("Ficha criada com sucesso", { position: "top-right" })
            }
            router.push(`/training-sheets/${programId}`)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState message="Carregando ficha..." />
    if (isEditing && error) return <ErrorState message="Erro ao carregar ficha" />

    return (
        <div className="flex flex-col gap-6">
            <EditPageHeader
                icon={CalendarDays}
                title={isEditing ? "Editar ficha" : "Nova ficha"}
                subtitle={isEditing ? "Atualize as informações da ficha" : "Preencha as informações da nova ficha"}
                onBack={() => router.push(`/training-sheets/${programId}`)}
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
                                <Input {...register("name")} placeholder="Ex: Treino A, Treino B..." />
                                <p className="text-sm text-red-500">{errors.name?.message}</p>
                            </Field>

                            <Field>
                                <Label>Descrição</Label>
                                <Textarea {...register("description")} placeholder="Descreva o foco desta ficha..." />
                                <p className="text-sm text-red-500">{errors.description?.message}</p>
                            </Field>

                            <Field>
                                <Label>Dias da semana</Label>
                                <Controller
                                    control={control}
                                    name="weekdays"
                                    render={({ field }) => (
                                        <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
                                            {weekDayOptions.map((day) => (
                                                <div key={day.value} className="flex items-center gap-2 rounded-md border px-3 py-2">
                                                    <Checkbox
                                                        id={day.value}
                                                        checked={field.value?.includes(day.value)}
                                                        onCheckedChange={(checked) => {
                                                            const current = field.value ?? []
                                                            if (checked) {
                                                                field.onChange([...current, day.value])
                                                            } else {
                                                                field.onChange(current.filter((d) => d !== day.value))
                                                            }
                                                        }}
                                                    />
                                                    <Label htmlFor={day.value} className="cursor-pointer text-sm">
                                                        {day.label}
                                                    </Label>
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                />
                                <p className="text-sm text-red-500">{errors.weekdays?.message}</p>
                            </Field>

                            <Field>
                                <Label>
                                    Tempo de descanso padrão <span className="text-muted-foreground text-xs">(segundos)</span>
                                </Label>
                                <Input
                                    type="number"
                                    placeholder="Ex: 60"
                                    {...register("restTimeSeconds", { valueAsNumber: true })}
                                />
                                <p className="text-sm text-red-500">{errors.restTimeSeconds?.message}</p>
                            </Field>

                            <Field>
                                <Label>Status</Label>
                                <Controller
                                    control={control}
                                    name="active"
                                    render={({ field }) => (
                                        <div className="flex items-center gap-3">
                                            <Switch checked={field.value} onCheckedChange={field.onChange} />
                                            <span className="text-sm text-muted-foreground">
                                                {field.value ? "Ativa" : "Inativa"}
                                            </span>
                                        </div>
                                    )}
                                />
                                <p className="text-sm text-red-500">{errors.active?.message}</p>
                            </Field>
                        </FieldGroup>
                    </CardContent>
                </Card>

                <div className="flex justify-end gap-2">
                    <Button type="button" variant="outline" onClick={() => router.push(`/training-sheets/${programId}`)}>
                        Cancelar
                    </Button>
                    <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                        {isEditing ? "Salvar alterações" : "Criar ficha"}
                    </Button>
                </div>
            </form>
        </div>
    )
}
