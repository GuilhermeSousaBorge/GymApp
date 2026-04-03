"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { StatusBadge } from "@/components/ui/status-badge"
import { useSheets } from "@/hooks/training-sheet"
import { translateWeekDays } from "@/lib/utils"
import { CalendarDays, Clock, Plus } from "lucide-react"
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

            {sheets.length === 0 ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Nenhuma ficha cadastrada
                    </CardContent>
                </Card>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {sheets.map((sheet) => (
                        <Card key={sheet.id} className="flex flex-col">
                            <CardHeader className="pb-3">
                                <div className="flex items-center justify-between">
                                    <CardTitle className="text-base font-semibold">{sheet.name}</CardTitle>
                                    <StatusBadge active={sheet.active} activeLabel="Ativa" inactiveLabel="Inativa" />
                                </div>
                            </CardHeader>
                            <CardContent className="flex flex-col gap-3 flex-1">
                                <div className="flex flex-col gap-2 text-sm flex-1">
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <CalendarDays className="h-3.5 w-3.5 shrink-0" />
                                        <span className="text-foreground">{translateWeekDays(sheet.weekdays) || "Sem dias definidos"}</span>
                                    </div>
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <Clock className="h-3.5 w-3.5 shrink-0" />
                                        <span>Descanso: <span className="text-foreground">{sheet.restTimeSeconds ? `${sheet.restTimeSeconds}s` : "-"}</span></span>
                                    </div>
                                </div>
                                <div className="flex gap-2 pt-2">
                                    <Button asChild variant="outline" size="sm" className="flex-1">
                                        <Link href={`/training-exercises/${sheet.id}`}>Exercícios</Link>
                                    </Button>
                                    <Button asChild variant="outline" size="sm" className="flex-1">
                                        <Link href={`/training-sheets/${programId}/${sheet.id}/edit`}>Editar</Link>
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    )
}
