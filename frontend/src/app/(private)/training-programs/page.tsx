"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { usePrograms } from "@/hooks/training-program"
import { useUsers } from "@/hooks/user"
import { formatDate, isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { ClipboardList, Plus, Search } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

const TrainingProgramPage = () => {
  const user = useUser()
  const { data: users = [], isLoading: isLoadingUsers, error: userError } = useUsers()
  const isAdminUser = isAdmin(user)
  const programParams = { userId: !isAdminUser ? user?.id : undefined }
  const { data: programs = [], isLoading: isLoadingPrograms, error: programError } = usePrograms(programParams)

  const [search, setSearch] = useState("")
  const [userFilter, setUserFilter] = useState("all")

  const filtered = programs.filter(program => {
    const matchesSearch = program.name.toLowerCase().includes(search.toLowerCase())
    const matchesUser = userFilter === "all" || String(program.student?.id) === userFilter
    return matchesSearch && matchesUser
  })

  if (isLoadingPrograms || isLoadingUsers) return <>Carregando ...</>
  if (programError || userError) return <>Deu erro ai...</>

  return (
    <div className="flex flex-col gap-6">

      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
            <ClipboardList className="h-5 w-5 text-primary" />
          </div>
          <div>
            <h1 className="text-xl font-bold">Programas de Treino</h1>
            <p className="text-sm text-muted-foreground">{filtered.length} programa(s) encontrado(s)</p>
          </div>
        </div>
        <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
          <Link href="/training-programs/new/edit">
            <Plus className="h-4 w-4 mr-2" />
            Novo programa
          </Link>
        </Button>
      </div>

      {/* Filtros */}
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
                {users.map(user => (
                  <SelectItem key={user.id} value={String(user.id)}>{user.name}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        </CardContent>
      </Card>

      {/* Tabela */}
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
                filtered.map(program => (
                  <TableRow key={program.id}>
                    <TableCell className="font-medium">{program.name}</TableCell>
                    <TableCell>
                      <Badge className={program.active
                        ? "bg-green-100 text-green-700 hover:bg-green-100"
                        : "bg-red-100 text-red-700 hover:bg-red-100"
                      }>
                        {program.active ? "Ativo" : "Inativo"}
                      </Badge>
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

export default TrainingProgramPage