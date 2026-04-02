"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { StatusBadge } from "@/components/ui/status-badge"
import { usePrograms } from "@/hooks/training-program"
import { useUsers } from "@/hooks/user"
import { formatDate, isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { ClipboardList, Plus, Search } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

export function TrainingProgramList() {
    const user = useUser()
    const isAdminUser = isAdmin(user)
    const programParams = { userId: !isAdminUser ? user?.id : undefined }

    const { data: users = [], isLoading: isLoadingUsers, error: userError } = useUsers()
    const { data: programs = [], isLoading: isLoadingPrograms, error: programError } = usePrograms(programParams)

    const [search, setSearch] = useState("")
    const [userFilter, setUserFilter] = useState("all")

    const filtered = programs.filter((program) => {
        const matchesSearch = program.name.toLowerCase().includes(search.toLowerCase())
        const matchesUser = userFilter === "all" || String(program.student?.id) === userFilter
        return matchesSearch && matchesUser
    })

    if (isLoadingPrograms || isLoadingUsers) return <LoadingState message="Carregando programas..." />
    if (programError || userError) return <ErrorState message="Erro ao carregar programas" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={ClipboardList}
                title="Programas de Treino"
                subtitle={`${filtered.length} programa(s) encontrado(s)`}
                action={{
                    label: "Novo programa",
                    href: "/training-programs/new/edit",
                    icon: Plus,
                }}
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
                        <Select value={userFilter} onValueChange={setUserFilter}>
                            <SelectTrigger className="w-52">
                                <SelectValue placeholder="Filtrar por aluno" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">Todos os alunos</SelectItem>
                                {users.map((u) => (
                                    <SelectItem key={u.id} value={String(u.id)}>{u.name}</SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    )}
                </CardContent>
            </Card>

            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Lista de programas</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Aluno</TableHead>
                                <TableHead>Instrutor</TableHead>
                                <TableHead>Criado em</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={6} className="text-center text-muted-foreground py-10">
                                        Nenhum programa encontrado
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map((program) => (
                                    <TableRow key={program.id}>
                                        <TableCell className="font-medium">{program.name}</TableCell>
                                        <TableCell>
                                            <StatusBadge active={program.active} />
                                        </TableCell>
                                        <TableCell>{program.student?.name ?? "-"}</TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {program.trainer?.name ?? "Sem personal"}
                                        </TableCell>
                                        <TableCell className="text-muted-foreground">
                                            {formatDate(new Date(program.createdAt))}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end gap-2">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-programs/${program.id}/edit`}>Editar</Link>
                                                </Button>
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/training-sheets/${program.id}`}>Fichas</Link>
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
