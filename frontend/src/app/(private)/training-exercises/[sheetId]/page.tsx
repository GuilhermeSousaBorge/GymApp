"use client"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useTrainingExercises } from "@/hooks/training-exercise"
import Link from "next/link"
import { useParams } from "next/navigation"


const TrainingExercisesPage = () => {
    const params = useParams()

    const { data: exercises = [], isLoading, error } = useTrainingExercises({ sheetId: Number(params.sheetId) })

    if (isLoading) return <>Carregando...</>

    if (error) return <>Deu erro ai...</>

    return (
        <div>
            <Card>
                <CardHeader>
                    <CardTitle className="flex justify-between">
                        Lista de Exercicios
                        <Button asChild>
                            <Link href={`/training-exercises/${params.sheetId}/new/edit`}>Adicionar exercicios</Link>
                        </Button>
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableCaption>
                            {exercises.length == 0 &&
                                <span>Essa ficha não possui exercicios</span>
                            }
                        </TableCaption>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Series</TableHead>
                                <TableHead>Repetições</TableHead>
                                <TableHead>Notas tecnicas</TableHead>
                                <TableHead>Açoes</TableHead>
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
                                            <Button asChild>
                                                <Link href={`/training-exercises/${params.sheetId}/${exercise.id}/edit`}>Ver detalhes</Link>
                                            </Button>
                                            <Button>Excluir</Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )
                            }
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}

export default TrainingExercisesPage