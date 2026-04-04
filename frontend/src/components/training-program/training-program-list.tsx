"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { ErrorState } from "@/components/ui/error-state"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select"
import { StatusBadge } from "@/components/ui/status-badge"
import { useExportProgramToPdf, usePrograms } from "@/hooks/training-program"
import { useUsers } from "@/hooks/user"
import { formatDate, isAdmin } from "@/lib/utils"
import { useUser } from "@/stores/auth"
import { Calendar, ClipboardList, Dumbbell, FileTextIcon, Plus, Search, User } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { toast } from "sonner"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog"
import { Field, FieldGroup } from "../ui/field"
import { Label } from "../ui/label"

export function TrainingProgramList() {
    const user = useUser()
    const isAdminUser = isAdmin(user)
    const programParams = { userId: !isAdminUser ? user?.id : undefined }

    const { data: users = [], isLoading: isLoadingUsers, error: userError } = useUsers()
    const { data: programs = [], isLoading: isLoadingPrograms, error: programError } = usePrograms(programParams)
    const { mutateAsync: exportPdf, isPending} = useExportProgramToPdf()

    const [search, setSearch] = useState("")
    const [userFilter, setUserFilter] = useState("all")
    const [selectedProgramId, setSelectedProgramId] = useState<number | null>(null)
    const [layout, setLayout] = useState<string>('medium')
    const isOpen = selectedProgramId !== null

    const filtered = programs.filter((program) => {
        const matchesSearch = program.name.toLowerCase().includes(search.toLowerCase())
        const matchesUser = userFilter === "all" || String(program.student?.id) === userFilter
        return matchesSearch && matchesUser
    })

    const handleOpen = (id: number) => {
        setSelectedProgramId(id)
    }

    const handleClose = () => {
        setSelectedProgramId(null)
    }

    const downloadPdf = () => {
        if (selectedProgramId == null) return
        toast.info("Gerando PDF, aguarde...")
        try{
            exportPdf({ programId: selectedProgramId, layout })
        }catch{
            toast.error("Erro ao gerar PDF")
        }finally{
            toast.success("PDF gerado com sucesso!")
            handleClose()
        }
    }

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
                                    <Button variant="outline" size="sm" className="flex-1 cursor-pointer" onClick={() => handleOpen(program.id)} disabled={isPending}>
                                        Exportar treino<FileTextIcon />
                                    </Button>
                                </div>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            )}
            <Dialog open={isOpen} onOpenChange={(open) => !open && handleClose()}>
                <DialogContent>
                <DialogHeader>
                            <DialogTitle>Faça o download do seu treino</DialogTitle>
                        </DialogHeader>
                        <FieldGroup className="py-4">
                            <Field>
                                <Label>Selecione o tipo de layout</Label>
                                <Select onValueChange={(e) => setLayout(e)} value={layout}>
                                <SelectTrigger className="w-full">
                                    <SelectValue placeholder="layout" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectGroup>
                                        <SelectLabel>layout</SelectLabel>
                                        <SelectItem value="simple">Simples</SelectItem>
                                        <SelectItem value="medium">medio</SelectItem>
                                        <SelectItem value="elaborated">Elaborado</SelectItem>
                                    </SelectGroup>
                                </SelectContent>
                                </Select>
                            </Field>
                        </FieldGroup>
                        <DialogFooter>
                            <DialogClose asChild>
                                <Button type="button" variant="outline">Cancelar</Button>
                            </DialogClose>
                            <Button variant="outline" size="sm" className="flex-1 cursor-pointer" onClick={() => downloadPdf()} disabled={isPending || selectedProgramId == null}>
                                Exportar treino<FileTextIcon />
                            </Button>
                        </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
