"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { DeleteConfirmDialog } from "@/components/ui/delete-confirm-dialog"
import { useDeleteTrainingExercise, useTrainingExercises } from "@/hooks/training-exercise"
import { handleMessageError } from "@/lib/handle-error"
import { Dumbbell, Plus } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { toast } from "sonner"

interface TrainingExerciseListProps {
    sheetId: number
}

export function TrainingExerciseList({ sheetId }: TrainingExerciseListProps) {
    const [deleteId, setDeleteId] = useState<number | null>(null)

    const { data: exercises = [], isLoading, error } = useTrainingExercises({ sheetId })
    const { mutateAsync: deleteExercise, isPending: isDeleting } = useDeleteTrainingExercise()

    const handleDelete = async () => {
        if (!deleteId) return
        try {
            await deleteExercise(deleteId)
            toast.success("Exercício removido com sucesso", { position: "top-right" })
            setDeleteId(null)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState />
    if (error) return <ErrorState />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Dumbbell}
                title="Lista de Exercícios"
                subtitle={`${exercises.length} exercício(s) na ficha`}
                action={{
                    label: "Adicionar exercícios",
                    href: `/training-exercises/${sheetId}/new/edit`,
                    icon: Plus,
                }}
            />

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Exercícios da ficha</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Séries</TableHead>
                                <TableHead>Repetições</TableHead>
                                <TableHead>Notas técnicas</TableHead>
                                <TableHead>Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {exercises.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} className="text-center text-muted-foreground py-10">
                                        Essa ficha não possui exercícios
                                    </TableCell>
                                </TableRow>
                            ) : (
                                exercises.map((exercise) => (
                                    <TableRow key={exercise.id}>
                                        <TableCell>{exercise.exerciseInfo?.name}</TableCell>
                                        <TableCell>{exercise.sets}</TableCell>
                                        <TableCell>{exercise.reps}</TableCell>
                                        <TableCell>{exercise.techniqueNotes}</TableCell>
                                        <TableCell>
                                            <div className="flex gap-2">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-exercises/${sheetId}/${exercise.id}/edit`}>
                                                        Ver detalhes
                                                    </Link>
                                                </Button>
                                                <Button
                                                    variant="destructive"
                                                    size="sm"
                                                    onClick={() => setDeleteId(exercise.id)}
                                                >
                                                    Excluir
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <DeleteConfirmDialog
                open={deleteId !== null}
                onOpenChange={(open) => !open && setDeleteId(null)}
                onConfirm={handleDelete}
                title="Excluir exercício"
                description="Tem certeza que deseja excluir este exercício da ficha? Esta ação não pode ser desfeita."
                isPending={isDeleting}
                confirmLabel="Confirmar exclusão"
                pendingLabel="Excluindo..."
            />
        </div>
    )
}
