"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { ErrorState } from "@/components/ui/error-state"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { LoadingState } from "@/components/ui/loading-state"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { EditPageHeader } from "@/components/ui/edit-page-header"
import { useExercises } from "@/hooks/exercise"
import { useCreateTrainingExericse, useTrainingExerciseDetails, useUpdateTrainingExercise } from "@/hooks/training-exercise"
import { handleMessageError } from "@/lib/handle-error"
import { cn } from "@/lib/utils"
import { TrainingExerciseFormData, trainingExerciseSchema } from "@/lib/validations/training-exercise"
import { zodResolver } from "@hookform/resolvers/zod"
import { Check, ChevronsUpDown, Dumbbell } from "lucide-react"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface TrainingExerciseEditFormProps {
    sheetId: number
    trainingExerciseId?: number
}

export function TrainingExerciseEditForm({ sheetId, trainingExerciseId }: TrainingExerciseEditFormProps) {
    const [open, setOpen] = useState(false)
    const router = useRouter()
    const isEditing = !!trainingExerciseId

    const { data: exercise, isLoading: isExerciseLoading, error: exerciseError } = useTrainingExerciseDetails(trainingExerciseId)
    const { data: exercises = [], isLoading: isExercisesLoading, error: exercisesError } = useExercises()

    const { mutateAsync: createTrainingexercise } = useCreateTrainingExericse()
    const { mutateAsync: updateTrainingExercise } = useUpdateTrainingExercise()

    const {
        register,
        handleSubmit,
        control,
        formState: { isSubmitting, errors },
    } = useForm<TrainingExerciseFormData>({
        resolver: zodResolver(trainingExerciseSchema),
        values: {
            exerciseId: exercise?.exerciseId ?? 0,
            reps: exercise?.reps ?? "",
            sets: exercise?.sets ?? 1,
            trainingSheetId: exercise?.trainingSheetId ?? sheetId,
            restTimeInSeconds: exercise?.restTimeInSeconds,
            techniqueNotes: exercise?.techniqueNotes,
        },
    })

    const onSubmit = async (data: TrainingExerciseFormData) => {
        try {
            if (isEditing) {
                await updateTrainingExercise({ id: trainingExerciseId!, exercise: data })
                toast.success("Exercício atualizado com sucesso", { position: "top-right" })
            } else {
                await createTrainingexercise(data)
                toast.success("Exercício criado com sucesso", { position: "top-right" })
            }
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isExerciseLoading || isExercisesLoading) return <LoadingState />
    if (exerciseError || exercisesError) return <ErrorState />

    return (
        <div className="flex flex-col gap-6">
            <EditPageHeader
                icon={Dumbbell}
                title={isEditing ? "Editar exercício" : "Adicionar exercício"}
                subtitle={isEditing ? "Atualize as informações do exercício" : "Preencha as informações do novo exercício"}
                onBack={() => router.push(`/training-exercises/${sheetId}`)}
            />

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Informações do exercício</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <FieldGroup>
                            <Field>
                                <Label>Exercício</Label>
                                <Controller
                                    name="exerciseId"
                                    control={control}
                                    render={({ field }) => {
                                        const selected = exercises.find((e) => e.id === field.value)
                                        return (
                                            <Popover open={open} onOpenChange={setOpen}>
                                                <PopoverTrigger asChild>
                                                    <Button
                                                        type="button"
                                                        variant="outline"
                                                        role="combobox"
                                                        className="w-full justify-between font-normal"
                                                    >
                                                        {selected ? selected.name : "Selecione o exercício"}
                                                        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-full p-0" align="start">
                                                    <Command>
                                                        <CommandInput placeholder="Buscar exercício..." />
                                                        <CommandList>
                                                            <CommandEmpty>Nenhum exercício encontrado.</CommandEmpty>
                                                            <CommandGroup>
                                                                {exercises.map((ex) => (
                                                                    <CommandItem
                                                                        key={ex.id}
                                                                        value={ex.name}
                                                                        onSelect={() => {
                                                                            field.onChange(ex.id)
                                                                            setOpen(false)
                                                                        }}
                                                                    >
                                                                        <Check
                                                                            className={cn(
                                                                                "mr-2 h-4 w-4",
                                                                                field.value === ex.id ? "opacity-100" : "opacity-0"
                                                                            )}
                                                                        />
                                                                        {ex.name}
                                                                    </CommandItem>
                                                                ))}
                                                            </CommandGroup>
                                                        </CommandList>
                                                    </Command>
                                                </PopoverContent>
                                            </Popover>
                                        )
                                    }}
                                />
                                <p className="text-sm text-red-500">{errors.exerciseId?.message}</p>
                            </Field>

                            <Field>
                                <Label>Numero de series</Label>
                                <Input {...register("sets", { valueAsNumber: true })} />
                                <p className="text-sm text-red-500">{errors.sets?.message}</p>
                            </Field>

                            <Field>
                                <Label>Numero de repetições</Label>
                                <Input {...register("reps")} />
                                <p className="text-sm text-red-500">{errors.reps?.message}</p>
                            </Field>

                            <Field>
                                <Label>Tempo de descanso entre series</Label>
                                <Input {...register("restTimeInSeconds", { valueAsNumber: true })} />
                                <p className="text-sm text-red-500">{errors.restTimeInSeconds?.message}</p>
                            </Field>

                            <Field>
                                <Label>Notas tecnicas (opcional)</Label>
                                <Input {...register("techniqueNotes")} />
                                <p className="text-sm text-red-500">{errors.techniqueNotes?.message}</p>
                            </Field>
                        </FieldGroup>

                        <div className="flex justify-end gap-2 mt-4">
                            <Button
                                type="button"
                                variant="outline"
                                onClick={() => router.push(`/training-exercises/${sheetId}`)}
                            >
                                Cancelar
                            </Button>
                            <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                                {isEditing ? "Salvar alterações" : "Adicionar"}
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}
