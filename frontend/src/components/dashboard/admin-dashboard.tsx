"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { useAdminDashboard } from "@/hooks/dashboard"
import { AlertCircle, Dumbbell, TrendingUp, UserCheck } from "lucide-react"

export const AdminDashboard = () => {
    const { data, isLoading, error } = useAdminDashboard()

    if (isLoading) return <LoadingState message="Carregando dashboard..." />
    if (error) return <ErrorState message="Erro ao carregar dashboard" />

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div>
                <h1 className="text-xl font-bold">Dashboard</h1>
                <p className="text-sm text-muted-foreground">Visão geral da academia</p>
            </div>

            {/* Métricas */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Alunos ativos
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center">
                            <UserCheck className="h-4 w-4 text-primary" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.totalActiveStudents}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Programas ativos
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-blue-100 flex items-center justify-center">
                            <Dumbbell className="h-4 w-4 text-blue-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.totalActivePrograms}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Novos alunos no mês
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                            <TrendingUp className="h-4 w-4 text-green-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.newStudentsThisMonth}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Sem programa
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-red-100 flex items-center justify-center">
                            <AlertCircle className="h-4 w-4 text-red-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.studentsWithoutProgram}</div>
                        <p className="text-xs text-muted-foreground mt-1">alunos sem treino</p>
                    </CardContent>
                </Card>

            </div>

            {/* Alunos sem programa — alerta */}
            {data && data?.studentsWithoutProgram > 0 && (
                <Card className="border-red-200 bg-red-50">
                    <CardContent className="flex items-center gap-3 pt-6">
                        <AlertCircle className="h-5 w-5 text-red-600 shrink-0" />
                        <p className="text-sm text-red-700">
                            <strong>{data?.studentsWithoutProgram}</strong> aluno(s) sem programa de treino. Considere atribuir um programa para eles.
                        </p>
                    </CardContent>
                </Card>
            )}

        </div>
    )
}