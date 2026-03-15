"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useStudentDashboard } from "@/hooks/dashboard"
import { useUser } from "@/stores/auth"
import { WEEKDAYS } from "@/types/training"
import { CalendarDays, ChevronRight, Dumbbell } from "lucide-react"
import Link from "next/link"

export const StudentDashboard = () => {
    const user = useUser()
    const { data, isLoading, error } = useStudentDashboard()

    if (isLoading) return <>Carregando dashboard...</>
    if (error) return <>Erro ao carregar dashboard</>

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div>
                <h1 className="text-xl font-bold">Olá, {user?.name?.split(" ")[0]} 👋</h1>
                <p className="text-sm text-muted-foreground">Aqui está seu resumo de treinos</p>
            </div>

            {/* Sem programas */}
            {data?.programs.length === 0 && (
                <Card>
                    <CardContent className="flex flex-col items-center gap-3 py-10 text-center">
                        <Dumbbell className="h-10 w-10 text-muted-foreground" />
                        <p className="text-muted-foreground text-sm">Você ainda não tem nenhum programa de treino.</p>
                        <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
                            <Link href="/training-programs/new/edit">Criar programa</Link>
                        </Button>
                    </CardContent>
                </Card>
            )}

            {/* Programas */}
            {data?.programs.map(program => (
                <div key={program.id} className="flex flex-col gap-4">

                    {/* Título do programa */}
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-2">
                            <h2 className="font-semibold">{program.name}</h2>
                            <Badge className={program.active
                                ? "bg-green-100 text-green-700 hover:bg-green-100"
                                : "bg-red-100 text-red-700 hover:bg-red-100"
                            }>
                                {program.active ? "Ativo" : "Inativo"}
                            </Badge>
                        </div>
                        <Button asChild variant="outline" size="sm">
                            <Link href={`/training-sheets/${program.id}`}>
                                Ver fichas
                                <ChevronRight className="h-3.5 w-3.5 ml-1" />
                            </Link>
                        </Button>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

                        {/* Ficha do dia */}
                        <Card>
                            <CardHeader className="flex flex-row items-center gap-2 pb-3">
                                <div className="h-8 w-8 rounded-lg bg-primary/10 flex items-center justify-center">
                                    <Dumbbell className="h-4 w-4 text-primary" />
                                </div>
                                <CardTitle className="text-sm font-semibold">Treino de hoje</CardTitle>
                            </CardHeader>
                            <CardContent>
                                {program.todaySheet ? (
                                    <div className="flex flex-col gap-2">
                                        <p className="font-medium">{program.todaySheet.name}</p>
                                        <p className="text-sm text-muted-foreground">{program.todaySheet.description}</p>
                                        <Button asChild variant="outline" size="sm" className="mt-2 w-fit">
                                            <Link href={`/training-exercises/${program.todaySheet.id}`}>
                                                Ver exercícios
                                            </Link>
                                        </Button>
                                    </div>
                                ) : (
                                    <p className="text-sm text-muted-foreground">Nenhum treino para hoje.</p>
                                )}
                            </CardContent>
                        </Card>

                        {/* Próximos treinos */}
                        <Card>
                            <CardHeader className="flex flex-row items-center gap-2 pb-3">
                                <div className="h-8 w-8 rounded-lg bg-blue-100 flex items-center justify-center">
                                    <CalendarDays className="h-4 w-4 text-blue-600" />
                                </div>
                                <CardTitle className="text-sm font-semibold">Próximos 7 dias</CardTitle>
                            </CardHeader>
                            <CardContent>
                                {program.nextTrainings.length === 0 ? (
                                    <p className="text-sm text-muted-foreground">Nenhum treino nos próximos 7 dias.</p>
                                ) : (
                                    <div className="flex flex-col gap-2">
                                        {program.nextTrainings.map(sheet => (
                                            <div key={sheet.id} className="flex items-center justify-between py-2 border-b last:border-0">
                                                <div>
                                                    <p className="text-sm font-medium">{sheet.name}</p>
                                                    <p className="text-xs text-muted-foreground">
                                                        {sheet.weekdays.map(d => WEEKDAYS[d]).join(", ")}
                                                    </p>
                                                </div>
                                                <Button asChild variant="ghost" size="sm">
                                                    <Link href={`/training-exercises/${sheet.id}`}>
                                                        <ChevronRight className="h-4 w-4" />
                                                    </Link>
                                                </Button>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </CardContent>
                        </Card>

                    </div>
                </div>
            ))}

        </div>
    )
}