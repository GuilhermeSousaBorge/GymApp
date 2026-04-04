"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { DeleteConfirmDialog } from "@/components/ui/delete-confirm-dialog"
import { useDeleteTrainingExercise, useTrainingExercises } from "@/hooks/training-exercise"
import { handleMessageError } from "@/lib/handle-error"
import { Dumbbell, Plus, Repeat, Layers, Clock, FileText } from "lucide-react"
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

            {exercises.length === 0 ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Essa ficha não possui exercícios
                    </CardContent>
                </Card>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {exercises.map((exercise) => (
                        <Card key={exercise.id} className="flex flex-col">
                            <CardHeader className="pb-3">
                                <CardTitle className="text-base font-semibold">
                                    {exercise.exerciseInfo?.name ?? "Exercício"}
                                </CardTitle>
                            </CardHeader>
                            <CardContent className="flex flex-col gap-3 flex-1">
                                <div className="flex flex-col gap-2 text-sm flex-1">
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <Layers className="h-3.5 w-3.5 shrink-0" />
                                        <span>Séries: <span className="text-foreground font-medium">{exercise.sets}</span></span>
                                    </div>
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <Repeat className="h-3.5 w-3.5 shrink-0" />
                                        <span>Repetições: <span className="text-foreground font-medium">{exercise.reps}</span></span>
                                    </div>
                                    {exercise.restTimeInSeconds && (
                                        <div className="flex items-center gap-2 text-muted-foreground">
                                            <Clock className="h-3.5 w-3.5 shrink-0" />
                                            <span>Descanso: <span className="text-foreground">{exercise.restTimeInSeconds}s</span></span>
                                        </div>
                                    )}
                                    {exercise.techniqueNotes && (
                                        <div className="flex items-start gap-2 text-muted-foreground">
                                            <FileText className="h-3.5 w-3.5 shrink-0 mt-0.5" />
                                            <span className="text-foreground text-xs leading-relaxed">{exercise.techniqueNotes}</span>
                                        </div>
                                    )}
                                </div>
                                <div className="flex gap-2 pt-2">
                                    <Button asChild variant="outline" size="sm" className="flex-1">
                                        <Link href={`/training-exercises/${sheetId}/${exercise.id}/edit`}>
                                            Ver detalhes
                                        </Link>
                                    </Button>
                                    <Button
                                        variant="destructive"
                                        size="sm"
                                        className="flex-1"
                                        onClick={() => setDeleteId(exercise.id)}
                                    >
                                        Excluir
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}

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
