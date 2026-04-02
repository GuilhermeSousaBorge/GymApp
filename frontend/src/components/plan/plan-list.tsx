"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { StatusBadge } from "@/components/ui/status-badge"
import { usePlans } from "@/hooks/plan"
import { formatCurrency } from "@/lib/format"
import { isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { Plan } from "@/types/plan"
import { Crown, Plus, Search } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

export function PlanList() {
    const [search, setSearch] = useState("")
    const [statusFilter, setStatusFilter] = useState<string>("all")

    const { data: plans = [], isLoading, error } = usePlans()
    const user = useUser()
    const isAdminUser = isAdmin(user)

    const filtered = plans.filter((plan: Plan) => {
        const matchesSearch = plan.name.toLowerCase().includes(search.toLowerCase())
        const matchesStatus = statusFilter === "all" || String(plan.active) === statusFilter
        return matchesSearch && matchesStatus
    })

    if (isLoading) return <LoadingState message="Carregando planos..." />
    if (error) return <ErrorState message="Erro ao carregar planos" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Crown}
                title="Planos"
                subtitle={`${filtered.length} plano(s) encontrado(s)`}
                action={
                    isAdminUser
                        ? { label: "Novo plano", href: "/plans/new/edit", icon: Plus }
                        : undefined
                }
            />

            <Card>
                <CardContent className="flex gap-4 pt-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Buscar por nome..."
                            className="pl-9"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                    {isAdminUser && (
                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-36">
                                <SelectValue placeholder="Status" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">Todos</SelectItem>
                                <SelectItem value="true">Ativo</SelectItem>
                                <SelectItem value="false">Inativo</SelectItem>
                            </SelectContent>
                        </Select>
                    )}
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Lista de planos</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Preço</TableHead>
                                <TableHead>Max Alunos</TableHead>
                                <TableHead>Max Programas</TableHead>
                                {isAdminUser && <TableHead>Status</TableHead>}
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={isAdminUser ? 6 : 5} className="text-center text-muted-foreground py-10">
                                        Nenhum plano encontrado
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map((plan) => (
                                    <TableRow key={plan.id}>
                                        <TableCell className="font-medium">{plan.name}</TableCell>
                                        <TableCell>{formatCurrency(plan.price)}</TableCell>
                                        <TableCell>{plan.maxStudents}</TableCell>
                                        <TableCell>{plan.maxPrograms}</TableCell>
                                        {isAdminUser && (
                                            <TableCell>
                                                <StatusBadge active={plan.active} />
                                            </TableCell>
                                        )}
                                        <TableCell className="text-right">
                                            <Button asChild variant="outline" size="sm">
                                                <Link href={`/plans/${plan.id}/edit`}>
                                                    {isAdminUser ? "Editar" : "Ver detalhes"}
                                                </Link>
                                            </Button>
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
