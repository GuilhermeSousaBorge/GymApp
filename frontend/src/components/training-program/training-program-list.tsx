"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { StatusBadge } from "@/components/ui/status-badge"
import { usePrograms } from "@/hooks/training-program"
import { useUsers } from "@/hooks/user"
import { formatDate, isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { ClipboardList, Plus, Search, User, Dumbbell, Calendar } from "lucide-react"
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

            {filtered.length === 0 ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Nenhum programa encontrado
                    </CardContent>
                </Card>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {filtered.map((program) => (
                        <Card key={program.id} className="flex flex-col">
                            <CardHeader className="pb-3">
                                <div className="flex items-center justify-between">
                                    <CardTitle className="text-base font-semibold">{program.name}</CardTitle>
                                    <StatusBadge active={program.active} />
                                </div>
                            </CardHeader>
                            <CardContent className="flex flex-col gap-3 flex-1">
                                <div className="flex flex-col gap-2 text-sm flex-1">
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <User className="h-3.5 w-3.5 shrink-0" />
                                        <span>Aluno: <span className="text-foreground">{program.student?.name ?? "-"}</span></span>
                                    </div>
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <Dumbbell className="h-3.5 w-3.5 shrink-0" />
                                        <span>Instrutor: <span className="text-foreground">{program.trainer?.name ?? "Sem personal"}</span></span>
                                    </div>
                                    <div className="flex items-center gap-2 text-muted-foreground">
                                        <Calendar className="h-3.5 w-3.5 shrink-0" />
                                        <span>{formatDate(new Date(program.createdAt))}</span>
                                    </div>
                                </div>
                                <div className="flex gap-2 pt-2">
                                    <Button asChild variant="outline" size="sm" className="flex-1">
                                        <Link href={`/training-programs/${program.id}/edit`}>Editar</Link>
                                    </Button>
                                    <Button asChild variant="outline" size="sm" className="flex-1">
                                        <Link href={`/training-sheets/${program.id}`}>Fichas</Link>
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
