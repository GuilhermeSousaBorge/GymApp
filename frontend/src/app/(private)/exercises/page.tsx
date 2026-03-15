"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useExercises } from "@/hooks/exercise"
import { useExerciseCategories } from "@/hooks/exerciseCategory"
import { Exercise } from "@/types/exercise"
import { Dumbbell, Plus, Search } from "lucide-react"
import Link from "next/link"
import { useState } from "react"

const ExercisePage = () => {
    const [search, setSearch] = useState("")
    const [categoryFilter, setCategoryFilter] = useState<string>("all")
    const [statusFilter, setStatusFilter] = useState<string>("all")

    const { data: exercises = [], isLoading, error } = useExercises()
    const { data: categories = [] } = useExerciseCategories()

    const filtered = exercises.filter((exercise: Exercise) => {
        const matchesSearch = exercise.name.toLowerCase().includes(search.toLowerCase())
        const matchesCategory = categoryFilter === "all" || String(exercise.categoryId) === categoryFilter
        const matchesStatus = statusFilter === "all" || String(exercise.active) === statusFilter
        return matchesSearch && matchesCategory && matchesStatus
    })

    if (isLoading) return <>Carregando dados</>
    if (error) return <>Erro ao carregar dados</>

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <Dumbbell className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">Exercícios</h1>
                        <p className="text-sm text-muted-foreground">{filtered.length} exercício(s) encontrado(s)</p>
                    </div>
                </div>
                <Button asChild className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <Link href="/exercises/new/edit">
                        <Plus className="h-4 w-4 mr-2" />
                        Novo exercício
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
                    <Select value={categoryFilter} onValueChange={setCategoryFilter}>
                        <SelectTrigger className="w-52">
                            <SelectValue placeholder="Categoria" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todas as categorias</SelectItem>
                            {categories.map(category => (
                                <SelectItem key={category.id} value={String(category.id)}>
                                    {category.muscleGroup}
                                </SelectItem>
                            ))}
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
                    <CardTitle className="text-base">Lista de exercícios</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nome</TableHead>
                                <TableHead>Categoria</TableHead>
                                <TableHead>Equipamento</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} className="text-center text-muted-foreground py-10">
                                        Nenhum exercício encontrado
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map(exercise => {
                                    const category = categories.find(c => c.id === exercise.categoryId)
                                    return (
                                        <TableRow key={exercise.id}>
                                            <TableCell className="font-medium">{exercise.name}</TableCell>
                                            <TableCell>
                                                {category ? (
                                                    <Badge variant="outline" className="text-xs">
                                                        {category.muscleGroup}
                                                    </Badge>
                                                ) : "-"}
                                            </TableCell>
                                            <TableCell className="text-muted-foreground">
                                                {exercise.equipment ?? "-"}
                                            </TableCell>
                                            <TableCell>
                                                <Badge className={exercise.active
                                                    ? "bg-green-100 text-green-700 hover:bg-green-100"
                                                    : "bg-red-100 text-red-700 hover:bg-red-100"
                                                }>
                                                    {exercise.active ? "Ativo" : "Inativo"}
                                                </Badge>
                                            </TableCell>
                                            <TableCell className="text-right">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/exercises/${exercise.id}/edit`}>
                                                        Ver detalhes
                                                    </Link>
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    )
                                })
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        </div>
    )
}

export default ExercisePage