"use client"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { useDeleteTrainingExercise, useTrainingExercises } from "@/hooks/training-exercise"
import { handleMessageError } from "@/lib/handle-error"
import Link from "next/link"
import { useParams } from "next/navigation"
import { useState } from "react"
import { toast } from "sonner"


const TrainingExercisesPage = () => {
    const params = useParams()
    const [deleteId, setDeleteId] = useState<number | null>(null)

    const { data: exercises = [], isLoading, error } = useTrainingExercises({ sheetId: Number(params.sheetId) })
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
        <div>
            <Card>
                <CardHeader>
                    <CardTitle className="flex justify-between">
                        Lista de Exercícios
                        <Button asChild>
                            <Link href={`/training-exercises/${params.sheetId}/new/edit`}>Adicionar exercícios</Link>
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableCaption>
                            {exercises.length == 0 &&
                                <span>Essa ficha não possui exercícios</span>
                            }
                        </TableCaption>
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
                            {exercises.length > 0 && (
                                exercises.map(exercise => (
                                    <TableRow key={exercise.id}>
                                        <TableCell>{exercise.exerciseInfo?.name}</TableCell>
                                        <TableCell>{exercise.sets}</TableCell>
                                        <TableCell>{exercise.reps}</TableCell>
                                        <TableCell>{exercise.techniqueNotes}</TableCell>
                                        <TableCell>
                                            <div className="flex gap-2">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-exercises/${params.sheetId}/${exercise.id}/edit`}>Ver detalhes</Link>
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
                            )
                            }
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            {/* Dialog de confirmação de exclusão */}
            <Dialog open={deleteId !== null} onOpenChange={(o) => !o && setDeleteId(null)}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Excluir exercício</DialogTitle>
                    </DialogHeader>
                    <p className="text-sm text-muted-foreground py-4">
                        Tem certeza que deseja excluir este exercício da ficha? Esta ação não pode ser desfeita.
                    </p>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button type="button" variant="outline">Cancelar</Button>
                        </DialogClose>
                        <Button variant="destructive" onClick={handleDelete} disabled={isDeleting}>
                            {isDeleting ? "Excluindo..." : "Confirmar exclusão"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}

export default TrainingExercisesPage
