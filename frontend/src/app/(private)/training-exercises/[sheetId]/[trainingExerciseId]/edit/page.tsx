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
import { useExercises } from "@/hooks/exercise"
import { useCreateTrainingExericse, useTrainingExerciseDetails, useUpdateTrainingExercise } from "@/hooks/training-exercise"
import { handleMessageError } from "@/lib/handle-error"
import { cn } from "@/lib/utils"
import { TrainingExerciseFormData, trainingExerciseSchema } from "@/lib/validations/training-exercise"
import { zodResolver } from "@hookform/resolvers/zod"
import { Check, ChevronsUpDown } from "lucide-react"
import { useParams, useRouter } from "next/navigation"
import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

const EditTrainingExercisePage = () => {

    const [open, setOpen] = useState(false)
    const params = useParams()
    const isEditing = params.trainingExerciseId !== "new"
    const router = useRouter()

    const { data: exercise, isLoading: isExerciseLoading, error: exerciseError } = useTrainingExerciseDetails(isEditing ? Number(params.trainingExerciseId) : undefined)

    const { data: exercises = [], isLoading: isExercisesLoading, error: exercisesError } = useExercises()

    const { mutateAsync: createTrainingexercise } = useCreateTrainingExericse()

    const { mutateAsync: updateTrainingExercise } = useUpdateTrainingExercise()

    const {
        register,
        handleSubmit,
        control,
        formState: { isSubmitting, errors }
    } = useForm<TrainingExerciseFormData>({
        resolver: zodResolver(trainingExerciseSchema),
        values: {
            exerciseId: exercise?.exerciseId ?? 0,
            reps: exercise?.reps ?? "",
            sets: exercise?.sets ?? 1,
            trainingSheetId: exercise?.trainingSheetId ?? Number(params.sheetId),
            restTimeInSeconds: exercise?.restTimeInSeconds,
            techniqueNotes: exercise?.techniqueNotes,
        }
    })

    const onSubmit = async (data: TrainingExerciseFormData) => {
        try {
            if (isEditing) {
                await updateTrainingExercise({ id: Number(params.trainingExerciseId), exercise: data })
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
        <div>
            <Card>
                <CardHeader>
                    <CardTitle>{isEditing ? "Editar" : "Criar"}</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <FieldGroup>
                            <Field>
                                <Controller
                                    name="exerciseId"
                                    control={control}
                                    render={({ field }) => {

                                        const selected = exercises.find(e => e.id === field.value)

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
                                                                {exercises.map((exercise) => (
                                                                    <CommandItem
                                                                        key={exercise.id}
                                                                        value={exercise.name}
                                                                        onSelect={() => {
                                                                            field.onChange(exercise.id)
                                                                            setOpen(false)
                                                                        }}
                                                                    >
                                                                        <Check className={cn(
                                                                            "mr-2 h-4 w-4",
                                                                            field.value === exercise.id ? "opacity-100" : "opacity-0"
                                                                        )} />
                                                                        {exercise.name}
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

                        <div className="flex justify-end gap-2">
                            <Button
                                type="button"
                                variant="outline"
                                className="bg-red-500"
                                onClick={() => router.push(`/training-exercises/${params.sheetId}`)}
                            >
                                Cancelar
                            </Button>
                            <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                                Salvar
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    )
}

export default EditTrainingExercisePage