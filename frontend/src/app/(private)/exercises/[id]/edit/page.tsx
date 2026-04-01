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
import { useCreateExercise, useExerciseDetails, useUpdateExercise } from "@/hooks/exercise"
import { useExerciseCategories } from "@/hooks/exerciseCategory"
import { handleMessageError } from "@/lib/handle-error"
import { ExerciseFormData, exerciseSchema } from "@/lib/validations/exercise"
import { zodResolver } from "@hookform/resolvers/zod"
import { ArrowLeft, Dumbbell } from "lucide-react"
import { useParams, useRouter } from "next/navigation"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

const ExerciseEditPage = () => {
    const router = useRouter()
    const params = useParams()
    const isEditing = params.id !== "new"

    const { data: exercise, isLoading, error } = useExerciseDetails(isEditing ? Number(params.id) : undefined)
    const { data: categories = [], isLoading: isLoadingCategory } = useExerciseCategories()
    const { mutateAsync: updateExercise } = useUpdateExercise()
    const { mutateAsync: createExercise } = useCreateExercise()

    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting }
    } = useForm<ExerciseFormData>({
        resolver: zodResolver(exerciseSchema),
        values: {
            name: exercise?.name ?? "",
            equipment: exercise?.equipment ?? "",
            description: exercise?.description ?? "",
            videoUrl: exercise?.videoUrl ?? "",
            active: exercise?.active ?? true,
            categoryId: exercise?.categoryId ?? 0
        }
    })

    const onSubmit = async (data: ExerciseFormData) => {
        try {
            if (isEditing) {
                await updateExercise({ id: Number(params.id), data })
                toast.success("Exercício atualizado com sucesso!")
            } else {
                await createExercise(data)
                toast.success("Exercício criado com sucesso!")
            }
            router.push("/exercises")
        } catch(err){
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading || isLoadingCategory) return <LoadingState message="Carregando exercício..." />
    if (isEditing && error) return <ErrorState message="Erro ao carregar exercício" />
    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div className="flex items-center gap-4">
                <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => router.push("/exercises")}
                >
                    <ArrowLeft className="h-4 w-4" />
                </Button>
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <Dumbbell className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">
                            {isEditing ? "Editar exercício" : "Novo exercício"}
                        </h1>
                        <p className="text-sm text-muted-foreground">
                            {isEditing ? "Atualize as informações do exercício" : "Preencha as informações do novo exercício"}
                        </p>
                    </div>
                </div>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Informações gerais</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <FieldGroup>
                            <Field>
                                <Label>Nome</Label>
                                <Input {...register("name")} />
                                <p className="text-sm text-red-500">{errors.name?.message}</p>
                            </Field>

                            <Field>
                                <Label>Equipamento</Label>
                                <Input {...register("equipment")} placeholder="Ex: Halteres, Barra, Máquina..." />
                                <p className="text-sm text-red-500">{errors.equipment?.message}</p>
                            </Field>

                            <Field>
                                <Label>Categoria</Label>
                                <Controller
                                    control={control}
                                    name="categoryId"
                                    render={({ field }) => {
                                        const categoryExists = categories.some(c => String(c.id) === String(field.value))
                                        const safeValue = (field.value && categoryExists) ? String(field.value) : ""
                                        return (
                                            <Select
                                                value={safeValue}
                                                onValueChange={(val) => {
                                                    const num = Number(val)
                                                    if (num > 0) field.onChange(num)
                                                }}
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Selecione uma categoria" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {categories.map((cat) => (
                                                        <SelectItem key={cat.id} value={String(cat.id)}>
                                                            {cat.muscleGroup}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        )
                                    }}
                                />
                                <p className="text-sm text-red-500">{errors.categoryId?.message}</p>
                            </Field>

                            <Field>
                                <Label>URL do vídeo</Label>
                                <Input {...register("videoUrl")} placeholder="https://youtube.com/..." />
                                <p className="text-sm text-red-500">{errors.videoUrl?.message}</p>
                            </Field>

                            <Field>
                                <Label>Descrição</Label>
                                <Textarea {...register("description")} placeholder="Descreva a execução do exercício..." />
                                <p className="text-sm text-red-500">{errors.description?.message}</p>
                            </Field>

                            <Field>
                                <Label>Ativo</Label>
                                <Controller
                                    control={control}
                                    name="active"
                                    render={({ field }) => (
                                        <div className="flex items-center gap-3">
                                            <Switch
                                                checked={field.value}
                                                onCheckedChange={field.onChange}
                                            />
                                            <span className="text-sm text-muted-foreground">
                                                {field.value ? "Ativo" : "Inativo"}
                                            </span>
                                        </div>
                                    )}
                                />
                            </Field>
                        </FieldGroup>
                    </CardContent>
                </Card>

                <div className="flex justify-end gap-2">
                    <Button
                        type="button"
                        variant="outline"
                        onClick={() => router.push("/exercises")}
                    >
                        Cancelar
                    </Button>
                    <Button type="submit" disabled={isSubmitting} className="bg-green-500">
                        {isEditing ? "Salvar alterações" : "Criar exercício"}
                    </Button>
                </div>
            </form>
        </div>
    )
}

export default ExerciseEditPage