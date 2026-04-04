"use client"

import Link from "next/link"
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
    ChartConfig,
    ChartContainer,
    ChartTooltip,
    ChartTooltipContent,
} from "@/components/ui/chart"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { useAdminDashboard } from "@/hooks/dashboard"
import {
    AlertCircle,
    CreditCard,
    DollarSign,
    Dumbbell,
    Plus,
    TrendingUp,
    UserCheck,
    UserPlus,
} from "lucide-react"

const chartConfig = {
    count: {
        label: "Alunos",
        color: "var(--chart-1)",
    },
} satisfies ChartConfig

function formatMonth(month: string): string {
    const [year, m] = month.split("-")
    const names = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"]
    return `${names[parseInt(m, 10) - 1]}/${year.slice(2)}`
}

function formatCurrency(value: number): string {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL",
    }).format(value)
}

function formatDate(dateStr: string): string {
    return new Intl.DateTimeFormat("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    }).format(new Date(dateStr))
}

export const AdminDashboard = () => {
    const { data, isLoading, error } = useAdminDashboard()

    if (isLoading) return <LoadingState message="Carregando dashboard..." />
    if (error) return <ErrorState message="Erro ao carregar dashboard" />

    const chartData = data?.studentsPerMonth?.map((item) => ({
        month: formatMonth(item.month),
        count: item.count,
    })) ?? []

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div>
                <h1 className="text-xl font-bold">Dashboard</h1>
                <p className="text-sm text-muted-foreground">Visao geral da academia</p>
            </div>

            {/* Metric Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">

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
                        <div className="text-3xl font-black">{data?.totalActiveStudents ?? 0}</div>
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
                        <div className="text-3xl font-black">{data?.totalActivePrograms ?? 0}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Novos no mes
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-green-100 flex items-center justify-center">
                            <TrendingUp className="h-4 w-4 text-green-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.newStudentsThisMonth ?? 0}</div>
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
                        <div className="text-3xl font-black">{data?.studentsWithoutProgram ?? 0}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Pag. pendentes
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-amber-100 flex items-center justify-center">
                            <CreditCard className="h-4 w-4 text-amber-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-3xl font-black">{data?.pendingPayments ?? 0}</div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader className="flex flex-row items-center justify-between pb-2">
                        <CardTitle className="text-sm font-medium text-muted-foreground">
                            Receita do mes
                        </CardTitle>
                        <div className="h-8 w-8 rounded-full bg-emerald-100 flex items-center justify-center">
                            <DollarSign className="h-4 w-4 text-emerald-600" />
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-black">
                            {formatCurrency(data?.monthlyRevenue ?? 0)}
                        </div>
                    </CardContent>
                </Card>

            </div>

            {/* Quick Actions */}
            <Card>
                <CardHeader className="pb-3">
                    <CardTitle className="text-base">Atalhos rapidos</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-wrap gap-2">
                    <Button asChild size="sm">
                        <Link href="/users/new/edit">
                            <UserPlus className="mr-2 h-4 w-4" />
                            Novo aluno
                        </Link>
                    </Button>
                    <Button asChild variant="outline" size="sm">
                        <Link href="/training-programs/new/edit">
                            <Dumbbell className="mr-2 h-4 w-4" />
                            Novo programa
                        </Link>
                    </Button>
                    <Button asChild variant="outline" size="sm">
                        <Link href="/payments">
                            <Plus className="mr-2 h-4 w-4" />
                            Pagamentos
                        </Link>
                    </Button>
                    <Button asChild variant="outline" size="sm">
                        <Link href="/subscriptions">
                            <CreditCard className="mr-2 h-4 w-4" />
                            Inscricoes
                        </Link>
                    </Button>
                </CardContent>
            </Card>

            {/* Chart + Recent Students (side-by-side on large screens) */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

                {/* Students per Month Chart */}
                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Evolucao de alunos</CardTitle>
                        <CardDescription>Novos alunos por mes (ultimos 6 meses)</CardDescription>
                    </CardHeader>
                    <CardContent>
                        {chartData.length > 0 ? (
                            <ChartContainer config={chartConfig} className="h-[250px] w-full">
                                <BarChart data={chartData} accessibilityLayer>
                                    <CartesianGrid vertical={false} />
                                    <XAxis
                                        dataKey="month"
                                        tickLine={false}
                                        tickMargin={10}
                                        axisLine={false}
                                    />
                                    <YAxis
                                        tickLine={false}
                                        axisLine={false}
                                        allowDecimals={false}
                                    />
                                    <ChartTooltip
                                        cursor={false}
                                        content={<ChartTooltipContent />}
                                    />
                                    <Bar
                                        dataKey="count"
                                        fill="var(--color-count)"
                                        radius={[4, 4, 0, 0]}
                                    />
                                </BarChart>
                            </ChartContainer>
                        ) : (
                            <p className="text-sm text-muted-foreground text-center py-8">
                                Sem dados disponíveis
                            </p>
                        )}
                    </CardContent>
                </Card>

                {/* Recent Students Table */}
                <Card>
                    <CardHeader>
                        <CardTitle className="text-base">Ultimos alunos</CardTitle>
                        <CardDescription>Alunos cadastrados recentemente</CardDescription>
                    </CardHeader>
                    <CardContent>
                        {data?.recentStudents && data.recentStudents.length > 0 ? (
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Nome</TableHead>
                                        <TableHead className="hidden sm:table-cell">E-mail</TableHead>
                                        <TableHead className="text-right">Data</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {data.recentStudents.map((student) => (
                                        <TableRow key={student.id}>
                                            <TableCell className="font-medium">
                                                <Link
                                                    href={`/users/${student.id}/edit`}
                                                    className="hover:underline"
                                                >
                                                    {student.name}
                                                </Link>
                                            </TableCell>
                                            <TableCell className="hidden sm:table-cell text-muted-foreground">
                                                {student.email}
                                            </TableCell>
                                            <TableCell className="text-right text-muted-foreground">
                                                {formatDate(student.createdAt)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        ) : (
                            <p className="text-sm text-muted-foreground text-center py-8">
                                Nenhum aluno cadastrado recentemente
                            </p>
                        )}
                    </CardContent>
                </Card>

            </div>

            {/* Alert: students without program */}
            {data && data.studentsWithoutProgram > 0 && (
                <Card className="border-red-200 bg-red-50">
                    <CardContent className="flex items-center gap-3 pt-6">
                        <AlertCircle className="h-5 w-5 text-red-600 shrink-0" />
                        <p className="text-sm text-red-700">
                            <strong>{data.studentsWithoutProgram}</strong> aluno(s) sem programa de treino.
                            Considere atribuir um programa para eles.
                        </p>
                        <Button asChild variant="outline" size="sm" className="ml-auto shrink-0">
                            <Link href="/training-programs/new/edit">Criar programa</Link>
                        </Button>
                    </CardContent>
                </Card>
            )}

        </div>
    )
}
