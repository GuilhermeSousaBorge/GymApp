"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { useUsers } from "@/hooks/user"
import { isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { Plus, Search, Users } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

const ListUsers = () => {

    const { data: users = [], isLoading, error } = useUsers()
    const user = useUser()
    const isAdminUser = isAdmin(user)
    const [search, setSearch] = useState("")
    const [roleFilter, setRoleFilter] = useState("all")
    const [statusFilter, setStatusFilter] = useState("all")

    

    const filtered = users.filter(user => {
        const matchesSearch = user.name.toLowerCase().includes(search.toLowerCase()) ||
            user.email.toLowerCase().includes(search.toLowerCase())
        const matchesRole = roleFilter === "all" || user.role?.name === roleFilter
        const matchesStatus = statusFilter === "all" || String(user.active) === statusFilter
        return matchesSearch && matchesRole && matchesStatus
    })

    if (isLoading) return <LoadingState message="Carregando usuários..." />
    if (error) return <ErrorState message="Erro ao carregar usuários" />
    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <Users className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">Usuários</h1>
                        <p className="text-sm text-muted-foreground">{filtered.length} usuário(s) encontrado(s)</p>
                    </div>
                </div>
               {isAdminUser && <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <Link href="/users/new/edit">
                        <Plus className="h-4 w-4 mr-2" />
                        Novo usuário
                    </Link>
                </Button>}
            </div>

            {/* Filtros */}
            <Card>
                <CardContent className="flex gap-4 pt-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Buscar por nome ou email..."
                            className="pl-9"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                    <Select value={roleFilter} onValueChange={setRoleFilter}>
                        <SelectTrigger className="w-48">
                            <SelectValue placeholder="Cargo" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todos os cargos</SelectItem>
                            <SelectItem value="Administrador">Administrador</SelectItem>
                            <SelectItem value="PersonalTrainer">Personal Trainer</SelectItem>
                            <SelectItem value="Recepcionista">Recepcionista</SelectItem>
                            <SelectItem value="Aluno">Aluno</SelectItem>
                        </SelectContent>
                    </Select>
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
                </CardContent>
            </Card>

            {/* Tabela */}
            <Card>
                <CardHeader>
                    <CardTitle className="text-base">Lista de usuários</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Email</TableHead>
                                <TableHead>Cargo</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} className="text-center text-muted-foreground py-10">
                                        Nenhum usuário encontrado
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map(user => (
                                    <TableRow key={user.id}>
                                        <TableCell className="font-medium">{user.name}</TableCell>
                                        <TableCell className="text-muted-foreground">{user.email}</TableCell>
                                        <TableCell>
                                            <Badge variant="outline">{user.role?.name ?? "-"}</Badge>
                                        </TableCell>
                                        <TableCell>
                                            <Badge className={user.active
                                                ? "bg-green-100 text-green-700 hover:bg-green-100"
                                                : "bg-red-100 text-red-700 hover:bg-red-100"
                                            }>
                                                {user.active ? "Ativo" : "Inativo"}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <Button asChild variant="outline" size="sm">
                                                <Link href={`/users/${user.id}/edit`}>Ver detalhes</Link>
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

export default ListUsers