"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { ErrorState } from "@/components/ui/error-state"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/loading-state"
import { PageHeader } from "@/components/ui/page-header"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { DeleteConfirmDialog } from "@/components/ui/delete-confirm-dialog"
import { CreateSubscriptionDialog } from "@/components/subscription/create-subscription-dialog"
import { useSubscriptions, useCancelSubscription } from "@/hooks/subscription"
import { handleMessageError } from "@/lib/handle-error"
import { formatCurrency } from "@/lib/format"
import { formatDate } from "@/lib/utils"
import { Subscription, SubscriptionStatus } from "@/types/subscription"
import { Banknote, CreditCard, Search, UserCheck, XCircle } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { toast } from "sonner"

const statusLabels: Record<SubscriptionStatus, string> = {
    ACTIVE: "Ativa",
    PAST_DUE: "Em atraso",
    CANCELLED: "Cancelada",
    EXPIRED: "Expirada",
}

const statusColors: Record<SubscriptionStatus, string> = {
    ACTIVE: "bg-green-100 text-green-700 hover:bg-green-100",
    PAST_DUE: "bg-yellow-100 text-yellow-700 hover:bg-yellow-100",
    CANCELLED: "bg-red-100 text-red-700 hover:bg-red-100",
    EXPIRED: "bg-gray-100 text-gray-700 hover:bg-gray-100",
}

export function AdminSubscriptionView() {
    const [showCreateDialog, setShowCreateDialog] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)
    const [selectedSubscription, setSelectedSubscription] = useState<Subscription | null>(null)
    const [search, setSearch] = useState("")
    const [statusFilter, setStatusFilter] = useState<string>("all")

    const queryStatus = statusFilter === "all" ? undefined : (statusFilter as SubscriptionStatus)
    const { data: subscriptions = [], isLoading, error } = useSubscriptions(queryStatus)
    const { mutateAsync: cancelSubscription, isPending: isCancelling } = useCancelSubscription()

    const filtered = subscriptions.filter((sub) =>
        sub.userName.toLowerCase().includes(search.toLowerCase()) ||
        sub.planName.toLowerCase().includes(search.toLowerCase())
    )

    const handleCancel = async () => {
        if (!selectedSubscription) return
        try {
            await cancelSubscription(selectedSubscription.id)
            toast.success("Inscrição cancelada com sucesso!", { position: "top-right" })
            setShowCancelDialog(false)
            setSelectedSubscription(null)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    const openCancelDialog = (subscription: Subscription) => {
        setSelectedSubscription(subscription)
        setShowCancelDialog(true)
    }

    if (isLoading) return <LoadingState message="Carregando inscrições..." />
    if (error) return <ErrorState message="Erro ao carregar inscrições" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={CreditCard}
                title="Inscrições"
                subtitle={`${filtered.length} inscrição(ões) encontrada(s)`}
                action={{
                    label: "Nova inscrição",
                    icon: UserCheck,
                    onClick: () => setShowCreateDialog(true),
                }}
            />

            <Card>
                <CardContent className="flex gap-4 pt-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Buscar por aluno ou plano..."
                            className="pl-9"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                    <Select value={statusFilter} onValueChange={setStatusFilter}>
                        <SelectTrigger className="w-52">
                            <SelectValue placeholder="Filtrar por status" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="all">Todos os status</SelectItem>
                            <SelectItem value="ACTIVE">Ativa</SelectItem>
                            <SelectItem value="PAST_DUE">Em atraso</SelectItem>
                            <SelectItem value="CANCELLED">Cancelada</SelectItem>
                            <SelectItem value="EXPIRED">Expirada</SelectItem>
                        </SelectContent>
                    </Select>
                </CardContent>
            </Card>

            <Card>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Aluno</TableHead>
                                <TableHead>Plano</TableHead>
                                <TableHead>Valor</TableHead>
                                <TableHead>Início</TableHead>
                                <TableHead>Fim</TableHead>
                                <TableHead>Renovação</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={8} className="text-center text-muted-foreground py-10">
                                        Nenhuma inscrição encontrada
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map((sub) => (
                                    <TableRow key={sub.id}>
                                        <TableCell className="font-medium">{sub.userName}</TableCell>
                                        <TableCell>{sub.planName}</TableCell>
                                        <TableCell>{formatCurrency(sub.planPriceAtStart)}</TableCell>
                                        <TableCell>{formatDate(new Date(sub.startDate))}</TableCell>
                                        <TableCell>{sub.endDate ? formatDate(new Date(sub.endDate)) : "Indeterminado"}</TableCell>
                                        <TableCell>{sub.autoRenew ? "Sim" : "Não"}</TableCell>
                                        <TableCell>
                                            <Badge className={statusColors[sub.status]}>
                                                {statusLabels[sub.status]}
                                            </Badge>
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end gap-1">
                                                <Button asChild variant="outline" size="sm">
                                                    <Link href={`/payments?subscriptionId=${sub.id}`}>
                                                        <Banknote className="h-3.5 w-3.5 mr-1" />
                                                        Pagamentos
                                                    </Link>
                                                </Button>
                                                {sub.status === "ACTIVE" && (
                                                    <Button
                                                        variant="outline"
                                                        size="sm"
                                                        onClick={() => openCancelDialog(sub)}
                                                    >
                                                        <XCircle className="h-3.5 w-3.5 mr-1" />
                                                        Cancelar
                                                    </Button>
                                                )}
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <DeleteConfirmDialog
                open={showCancelDialog}
                onOpenChange={setShowCancelDialog}
                onConfirm={handleCancel}
                title="Cancelar inscrição"
                description={`Tem certeza que deseja cancelar a inscrição de ${selectedSubscription?.userName} no plano ${selectedSubscription?.planName}? Esta ação não pode ser desfeita.`}
                isPending={isCancelling}
                confirmLabel="Confirmar cancelamento"
                pendingLabel="Cancelando..."
            />

            <CreateSubscriptionDialog
                open={showCreateDialog}
                onClose={() => setShowCreateDialog(false)}
            />
        </div>
    )
}
