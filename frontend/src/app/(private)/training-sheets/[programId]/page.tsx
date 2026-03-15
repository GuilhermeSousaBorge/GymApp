"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useSheets } from "@/hooks/training-sheet"
import { translateWeekDays } from "@/lib/utils"
import { CalendarDays, Plus } from "lucide-react"
import Link from "next/link"
import { useParams } from "next/navigation"

const SheetsPage = () => {
    const params = useParams()
    const { data: sheets = [], isLoading, error } = useSheets({ programId: Number(params.programId) })

    if (isLoading) return <>Carregando...</>
    if (error) return <>Deu erro ai...</>

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <CalendarDays className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">Fichas de Treino</h1>
                        <p className="text-sm text-muted-foreground">{sheets.length} ficha(s) cadastrada(s)</p>
                    </div>
                </div>
                <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <Link href={`/training-sheets/${params.programId}/new/edit`}>
                        <Plus className="h-4 w-4 mr-2" />
                        Nova ficha
                    </Link>
                </Button>
            </div>

            {/* Tabela */}
            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Lista de fichas</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Dias da semana</TableHead>
                                <TableHead>Descanso padrão</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {sheets.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} className="text-center text-muted-foreground py-10">
                                        Nenhuma ficha cadastrada
                                    </TableCell>
                                </TableRow>
                            ) : (
                                sheets.map(sheet => (
                                    <TableRow key={sheet.id}>
                                        <TableCell className="font-medium">{sheet.name}</TableCell>
                                        <TableCell className="text-muted-foreground text-sm">
                                            {translateWeekDays(sheet.weekdays)}
                                        </TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {sheet.restTimeSeconds ? `${sheet.restTimeSeconds}s` : "-"}
                                        </TableCell>
                                        <TableCell>
                                            <Badge className={sheet.active
                                                ? "bg-green-100 text-green-700 hover:bg-green-100"
                                                : "bg-red-100 text-red-700 hover:bg-red-100"
                                            }>
                                                {sheet.active ? "Ativa" : "Inativa"}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-exercises/${sheet.id}`}>Exercícios</Link>
                                                </Button>
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-sheets/${params.programId}/${sheet.id}/edit`}>Editar</Link>
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
        </div>
    )
}

export default SheetsPage