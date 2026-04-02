"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { useCreateCategory, useExerciseCategories, useUpdateCategory } from "@/hooks/exerciseCategory"
import { handleMessageError } from "@/lib/handle-error"
import { isAdmin } from "@/lib/utils"
import { ExerciseCategoryFormData, exerciseCategorySchema } from "@/lib/validations/exericiseCategory"
import { useUser } from "@/stores/auth"
import { ExerciseCategory } from "@/types/exercise-category"
import { zodResolver } from "@hookform/resolvers/zod"
import { Layers, Pencil, Plus } from "lucide-react"
import { useState } from "react"
import { useForm } from "react-hook-form"
import { toast } from "sonner"

export function CategoryList() {
    const [selectedCategory, setSelectedCategory] = useState<ExerciseCategory | null>(null)
    const [isCreating, setIsCreating] = useState(false)
    const isOpen = selectedCategory !== null || isCreating

    const { data: categories = [], isLoading, error } = useExerciseCategories()
    const user = useUser()
    const isAdminUser = isAdmin(user)

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<ExerciseCategoryFormData>({ resolver: zodResolver(exerciseCategorySchema) })

    const { mutateAsync: createCategory } = useCreateCategory()
    const { mutateAsync: updateCategory } = useUpdateCategory()

    const handleEdit = (category: ExerciseCategory) => {
        setSelectedCategory(category)
        reset({ muscleGroup: category.muscleGroup, description: category.description ?? "" })
    }

    const handleCreate = () => {
        setIsCreating(true)
        reset({ muscleGroup: "", description: "" })
    }

    const handleClose = () => {
        setSelectedCategory(null)
        setIsCreating(false)
        reset()
    }

    const onSubmit = async (data: ExerciseCategoryFormData) => {
        try {
            if (isCreating) {
                await createCategory(data)
                toast.success("Categoria criada com sucesso", { position: "top-right" })
            } else {
                await updateCategory({ id: selectedCategory!.id, data })
                toast.success("Categoria atualizada com sucesso", { position: "top-right" })
            }
            handleClose()
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState message="Carregando categorias..." />
    if (error) return <ErrorState message="Erro ao carregar categorias" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Layers}
                title="Categorias de exercícios"
                subtitle={`${categories.length} categoria(s) cadastrada(s)`}
                action={{ label: "Nova categoria", icon: Plus, onClick: handleCreate }}
            />

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Lista de categorias</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Grupo muscular</TableHead>
                                <TableHead>Descrição</TableHead>
                                {isAdminUser && <TableHead className="text-right">Ações</TableHead>}
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {categories.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={3} className="text-center text-muted-foreground py-10">
                                        Nenhuma categoria cadastrada
                                    </TableCell>
                                </TableRow>
                            ) : (
                                categories.map((category) => (
                                    <TableRow key={category.id}>
                                        <TableCell>
                                            <Badge variant="outline">{category.muscleGroup}</Badge>
                                        </TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {category.description || "-"}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            {isAdminUser && (
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => handleEdit(category)}
                                                >
                                                    <Pencil className="h-3.5 w-3.5 mr-1" />
                                                    Editar
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <Dialog open={isOpen} onOpenChange={(open) => !open && handleClose()}>
                <DialogContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <DialogHeader>
                            <DialogTitle>
                                {isCreating ? "Nova categoria" : "Editar categoria"}
                            </DialogTitle>
                        </DialogHeader>
                        <FieldGroup className="py-4">
                            <Field>
                                <Label>Grupo muscular</Label>
                                <Input {...register("muscleGroup")} placeholder="Ex: Peito, Costas, Pernas..." />
                                <p className="text-sm text-red-500">{errors.muscleGroup?.message}</p>
                            </Field>
                            <Field>
                                <Label>Descrição</Label>
                                <Input {...register("description")} placeholder="Descrição opcional" />
                                <p className="text-sm text-red-500">{errors.description?.message}</p>
                            </Field>
                        </FieldGroup>
                        <DialogFooter>
                            <DialogClose asChild>
                                <Button type="button" variant="outline">Cancelar</Button>
                            </DialogClose>
                            <Button type="submit" className="bg-green-500" disabled={isSubmitting}>
                                {isCreating ? "Criar" : "Salvar"}
                            </Button>
                        </DialogFooter>
                    </form>
                </DialogContent>
            </Dialog>
        </div>
    )
}
