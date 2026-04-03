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
import { CreatePaymentDialog } from "@/components/payment/create-payment-dialog"
import { usePayments, usePaymentsBySubscription, useMarkPaymentAsPaid } from "@/hooks/payment"
import { handleMessageError } from "@/lib/handle-error"
import { formatCurrency } from "@/lib/format"
import { formatDate } from "@/lib/utils"
import { PaymentMethod, PaymentStatus } from "@/types/payment"
import { Banknote, CheckCircle, Plus, Search } from "lucide-react"
import { useSearchParams } from "next/navigation"
import { useState } from "react"
import { toast } from "sonner"

const statusLabels: Record<PaymentStatus, string> = {
    PENDING: "Pendente",
    PAID: "Pago",
    FAILED: "Falhou",
    CANCELLED: "Cancelado",
    REFUNDED: "Reembolsado",
}

const statusColors: Record<PaymentStatus, string> = {
    PENDING: "bg-yellow-100 text-yellow-700 hover:bg-yellow-100",
    PAID: "bg-green-100 text-green-700 hover:bg-green-100",
    FAILED: "bg-red-100 text-red-700 hover:bg-red-100",
    CANCELLED: "bg-gray-100 text-gray-700 hover:bg-gray-100",
    REFUNDED: "bg-blue-100 text-blue-700 hover:bg-blue-100",
}

const methodLabels: Record<PaymentMethod, string> = {
    PIX: "PIX",
    CREDIT_CARD: "Cartão de Crédito",
    BOLETO: "Boleto",
    CASH: "Dinheiro",
}

export function AdminPaymentsView() {
    const searchParams = useSearchParams()
    const preselectedSubId = searchParams.get("subscriptionId")

    const [showCreateDialog, setShowCreateDialog] = useState(false)
    const [statusFilter, setStatusFilter] = useState<string>("all")
    const [search, setSearch] = useState("")

    const queryStatus = statusFilter === "all" ? undefined : (statusFilter as PaymentStatus)

    // Se veio com subscriptionId na URL, carrega pagamentos dessa inscricao
    // Senao, carrega todos os pagamentos com filtro opcional por status
    const subscriptionId = preselectedSubId ? Number(preselectedSubId) : undefined
    const {
        data: allPayments = [],
        isLoading: isLoadingAll,
        error: errorAll,
    } = usePayments(subscriptionId ? undefined : queryStatus)
    const {
        data: subPayments = [],
        isLoading: isLoadingSub,
        error: errorSub,
    } = usePaymentsBySubscription(subscriptionId)

    const { mutateAsync: markAsPaid } = useMarkPaymentAsPaid()

    const payments = subscriptionId ? subPayments : allPayments
    const isLoading = subscriptionId ? isLoadingSub : isLoadingAll
    const error = subscriptionId ? errorSub : errorAll

    const filtered = payments.filter((p) => {
        if (!search) return true
        return String(p.subscriptionId).includes(search) || String(p.id).includes(search)
    })

    const handleMarkAsPaid = async (paymentId: number) => {
        try {
            await markAsPaid(paymentId)
            toast.success("Pagamento marcado como pago!", { position: "top-right" })
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    if (isLoading) return <LoadingState message="Carregando pagamentos..." />
    if (error) return <ErrorState message="Erro ao carregar pagamentos" />

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Banknote}
                title="Pagamentos"
                subtitle={subscriptionId
                    ? `Pagamentos da inscrição #${subscriptionId}`
                    : `${filtered.length} pagamento(s) encontrado(s)`
                }
                action={subscriptionId ? {
                    label: "Registrar pagamento",
                    icon: Plus,
                    onClick: () => setShowCreateDialog(true),
                } : undefined}
            />

            <Card>
                <CardContent className="flex gap-4 pt-6">
                    <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Buscar por ID do pagamento ou inscrição..."
                            className="pl-9"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                    {!subscriptionId && (
                        <Select value={statusFilter} onValueChange={setStatusFilter}>
                            <SelectTrigger className="w-52">
                                <SelectValue placeholder="Filtrar por status" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">Todos os status</SelectItem>
                                <SelectItem value="PENDING">Pendente</SelectItem>
                                <SelectItem value="PAID">Pago</SelectItem>
                                <SelectItem value="FAILED">Falhou</SelectItem>
                                <SelectItem value="CANCELLED">Cancelado</SelectItem>
                                <SelectItem value="REFUNDED">Reembolsado</SelectItem>
                            </SelectContent>
                        </Select>
                    )}
                </CardContent>
            </Card>

            <Card>
                <CardContent className="p-0">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>ID</TableHead>
                                <TableHead>Inscrição</TableHead>
                                <TableHead>Vencimento</TableHead>
                                <TableHead>Valor</TableHead>
                                <TableHead>Método</TableHead>
                                <TableHead>Status</TableHead>
                                <TableHead>Data Pagamento</TableHead>
                                <TableHead className="text-right">Ações</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {filtered.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={8} className="text-center text-muted-foreground py-10">
                                        Nenhum pagamento encontrado
                                    </TableCell>
                                </TableRow>
                            ) : (
                                filtered.map((payment) => (
                                    <TableRow key={payment.id}>
                                        <TableCell className="font-medium">#{payment.id}</TableCell>
                                        <TableCell>#{payment.subscriptionId}</TableCell>
                                        <TableCell>{formatDate(new Date(payment.dueDate))}</TableCell>
                                        <TableCell className="font-medium">{formatCurrency(payment.amount)}</TableCell>
                                        <TableCell>{methodLabels[payment.paymentMethod]}</TableCell>
                                        <TableCell>
                                            <Badge className={statusColors[payment.status]}>
                                                {statusLabels[payment.status]}
                                            </Badge>
                                        </TableCell>
                                        <TableCell>
                                            {payment.paymentDate ? formatDate(new Date(payment.paymentDate)) : "-"}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            {payment.status === "PENDING" && (
                                                <Button
                                                    variant="outline"
                                                    size="sm"
                                                    onClick={() => handleMarkAsPaid(payment.id)}
                                                >
                                                    <CheckCircle className="h-3.5 w-3.5 mr-1" />
                                                    Marcar como pago
                                                </Button>
                                            )}
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            {subscriptionId && (
                <CreatePaymentDialog
                    open={showCreateDialog}
                    onClose={() => setShowCreateDialog(false)}
                    subscriptionId={subscriptionId}
                />
            )}
        </div>
    )
}
