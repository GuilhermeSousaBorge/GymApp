"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { StatusBadge } from "@/components/ui/status-badge"
import { useSheets } from "@/hooks/training-sheet"
import { translateWeekDays } from "@/lib/utils"
import { CalendarDays, Plus } from "lucide-react"
import Link from "next/link"

interface TrainingSheetListProps {
    programId: number
}

export function TrainingSheetList({ programId }: TrainingSheetListProps) {
    const { data: sheets = [], isLoading, error } = useSheets({ programId })

    if (isLoading) return <LoadingState message="Carregando fichas..." />
    if (error) return <ErrorState message="Erro ao carregar fichas" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={CalendarDays}
                title="Fichas de Treino"
                subtitle={`${sheets.length} ficha(s) cadastrada(s)`}
                action={{
                    label: "Nova ficha",
                    href: `/training-sheets/${programId}/new/edit`,
                    icon: Plus,
                }}
            />

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
                                sheets.map((sheet) => (
                                    <TableRow key={sheet.id}>
                                        <TableCell className="font-medium">{sheet.name}</TableCell>
                                        <TableCell className="text-muted-foreground text-sm">
                                            {translateWeekDays(sheet.weekdays)}
                                        </TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {sheet.restTimeSeconds ? `${sheet.restTimeSeconds}s` : "-"}
                                        </TableCell>
                                        <TableCell>
                                            <StatusBadge active={sheet.active} activeLabel="Ativa" inactiveLabel="Inativa" />
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-exercises/${sheet.id}`}>Exercícios</Link>
                                                </Button>
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-sheets/${programId}/${sheet.id}/edit`}>Editar</Link>
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
