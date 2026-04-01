"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { LoadingState } from "@/components/ui/loading-state"
import { useCreatePayment, useMarkPaymentAsPaid, usePaymentsBySubscription } from "@/hooks/payment"
import { useActiveSubscription, useMySubscription } from "@/hooks/subscription"
import { useUsers } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { formatDate, isAdmin, isStudent } from "@/lib/utils"
import { PaymentFormData, paymentSchema } from "@/lib/validations/payment"
import { useUser } from "@/stores/auth"
import { PaymentMethod, PaymentStatus } from "@/types/payment"
import { zodResolver } from "@hookform/resolvers/zod"
import { Banknote, CheckCircle, Plus, Search } from "lucide-react"
import { useSearchParams } from "next/navigation"
import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
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

const formatCurrency = (value: number) =>
    new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value)

const PaymentsPage = () => {
    const user = useUser()
    const isStudentUser = isStudent(user)

    if (isStudentUser) {
        return <StudentPaymentsView />
    }

    return <AdminPaymentsView />
}

// === VISÃO DO ALUNO ===
const StudentPaymentsView = () => {
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useMySubscription()
    const subscriptionId = subscription?.id
    const { data: payments = [], isLoading: isLoadingPayments } = usePaymentsBySubscription(subscriptionId)

    if (isLoadingSub) return <LoadingState message="Carregando dados..." />
    const hasSubscription = !subError && subscription

    return (
        <div className="flex flex-col gap-6">
            <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                    <Banknote className="h-5 w-5 text-primary" />
                </div>
                <div>
                    <h1 className="text-xl font-bold">Meus Pagamentos</h1>
                    <p className="text-sm text-muted-foreground">
                        {hasSubscription
                            ? `Pagamentos do plano ${subscription.planName}`
                            : "Visualize seus pagamentos"
                        }
                    </p>
                </div>
            </div>

            {!hasSubscription ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Você não possui uma inscrição ativa. Não há pagamentos para exibir.
                    </CardContent>
                </Card>
            ) : isLoadingPayments ? (
                <LoadingState message="Carregando pagamentos..." />
            ) : (
                <PaymentsTable payments={payments} showActions={false} />
            )}
        </div>
    )
}

// === VISÃO DO ADMIN / TRAINER ===
const AdminPaymentsView = () => {
    const searchParams = useSearchParams()
    const preselectedSubId = searchParams.get("subscriptionId")

    const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined)
    const [userSearch, setUserSearch] = useState("")
    const [showCreateDialog, setShowCreateDialog] = useState(false)

    const { data: users = [] } = useUsers()
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useActiveSubscription(selectedUserId)

    // Se veio com subscriptionId na URL, usa direto
    const effectiveSubscriptionId = preselectedSubId ? Number(preselectedSubId) : subscription?.id
    const { data: payments = [], isLoading: isLoadingPayments } = usePaymentsBySubscription(effectiveSubscriptionId)
    const { mutateAsync: markAsPaid } = useMarkPaymentAsPaid()

    const hasSubscription = (preselectedSubId && effectiveSubscriptionId) || (selectedUserId && !subError && subscription)

    const filteredUsers = users.filter(u =>
        u.name.toLowerCase().includes(userSearch.toLowerCase()) ||
        u.email.toLowerCase().includes(userSearch.toLowerCase())
    )

    const handleMarkAsPaid = async (paymentId: number) => {
        try {
            await markAsPaid(paymentId)
            toast.success("Pagamento marcado como pago!", { position: "top-right" })
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="flex flex-col gap-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <Banknote className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">Pagamentos</h1>
                        <p className="text-sm text-muted-foreground">Gerencie os pagamentos dos alunos</p>
                    </div>
                </div>
                {hasSubscription && (
                    <Button onClick={() => setShowCreateDialog(true)} className="bg-primary text-primary-foreground hover:bg-primary/90">
                        <Plus className="h-4 w-4 mr-2" />
                        Registrar pagamento
                    </Button>
                )}
            </div>

            {/* Seletor de aluno (se não veio da URL) */}
            {!preselectedSubId && (
                <Card>
                    <CardContent className="pt-6">
                        <Field>
                            <Label>Selecionar aluno</Label>
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                                <Input
                                    placeholder="Buscar aluno por nome ou email..."
                                    className="pl-9 mb-2"
                                    value={userSearch}
                                    onChange={(e) => setUserSearch(e.target.value)}
                                />
                            </div>
                            {userSearch.length >= 2 && (
                                <div className="border rounded-md max-h-48 overflow-y-auto">
                                    {filteredUsers.length === 0 ? (
                                        <p className="p-3 text-sm text-muted-foreground">Nenhum usuário encontrado</p>
                                    ) : (
                                        filteredUsers.slice(0, 10).map(u => (
                                            <button
                                                key={u.id}
                                                type="button"
                                                className={`w-full text-left px-3 py-2 hover:bg-muted text-sm ${selectedUserId === u.id ? "bg-primary/10 font-medium" : ""}`}
                                                onClick={() => {
                                                    setSelectedUserId(u.id)
                                                    setUserSearch(u.name)
                                                }}
                                            >
                                                <span className="font-medium">{u.name}</span>
                                                <span className="text-muted-foreground ml-2">{u.email}</span>
                                            </button>
                                        ))
                                    )}
                                </div>
                            )}
                        </Field>
                    </CardContent>
                </Card>
            )}

            {/* Estado de loading */}
            {selectedUserId && isLoadingSub && <LoadingState message="Carregando inscrição..." />}

            {/* Sem inscrição */}
            {selectedUserId && !isLoadingSub && !hasSubscription && (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Este aluno não possui uma inscrição ativa. Não há pagamentos para exibir.
                    </CardContent>
                </Card>
            )}

            {/* Tabela de pagamentos */}
            {hasSubscription && (
                isLoadingPayments ? (
                    <LoadingState message="Carregando pagamentos..." />
                ) : (
                    <PaymentsTable
                        payments={payments}
                        showActions={true}
                        onMarkAsPaid={handleMarkAsPaid}
                    />
                )
            )}

            {/* Dialog de criação */}
            {effectiveSubscriptionId && (
                <CreatePaymentDialog
                    open={showCreateDialog}
                    onClose={() => setShowCreateDialog(false)}
                    subscriptionId={effectiveSubscriptionId}
                />
            )}
        </div>
    )
}

// === TABELA DE PAGAMENTOS ===
const PaymentsTable = ({
    payments,
    showActions,
    onMarkAsPaid,
}: {
    payments: { id: number; dueDate: string; amount: number; paymentMethod: PaymentMethod; status: PaymentStatus; paymentDate: string | null }[]
    showActions: boolean
    onMarkAsPaid?: (id: number) => void
}) => (
    <Card>
        <CardHeader>
            <CardTitle className="text-base">Lista de pagamentos</CardTitle>
        </CardHeader>
        <CardContent>
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Vencimento</TableHead>
                        <TableHead>Valor</TableHead>
                        <TableHead>Método</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Data Pagamento</TableHead>
                        {showActions && <TableHead className="text-right">Ações</TableHead>}
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {payments.length === 0 ? (
                        <TableRow>
                            <TableCell colSpan={showActions ? 6 : 5} className="text-center text-muted-foreground py-10">
                                Nenhum pagamento encontrado
                            </TableCell>
                        </TableRow>
                    ) : (
                        payments.map(payment => (
                            <TableRow key={payment.id}>
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
                                {showActions && (
                                    <TableCell className="text-right">
                                        {payment.status === "PENDING" && onMarkAsPaid && (
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                onClick={() => onMarkAsPaid(payment.id)}
                                            >
                                                <CheckCircle className="h-3.5 w-3.5 mr-1" />
                                                Marcar como pago
                                            </Button>
                                        )}
                                    </TableCell>
                                )}
                            </TableRow>
                        ))
                    )}
                </TableBody>
            </Table>
        </CardContent>
    </Card>
)

// === DIALOG DE CRIAÇÃO DE PAGAMENTO ===
const CreatePaymentDialog = ({
    open,
    onClose,
    subscriptionId,
}: {
    open: boolean
    onClose: () => void
    subscriptionId: number
}) => {
    const { mutateAsync: createPayment } = useCreatePayment()

    const {
        register,
        handleSubmit,
        control,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<PaymentFormData>({
        resolver: zodResolver(paymentSchema),
        values: {
            subscriptionId,
            amount: 0,
            dueDate: "",
            paymentMethod: "PIX",
        }
    })

    const onSubmit = async (data: PaymentFormData) => {
        try {
            await createPayment(data)
            toast.success("Pagamento registrado com sucesso!", { position: "top-right" })
            reset()
            onClose()
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
            <DialogContent>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogHeader>
                        <DialogTitle>Registrar pagamento</DialogTitle>
                    </DialogHeader>
                    <FieldGroup className="py-4">
                        <Field>
                            <Label>Valor (R$)</Label>
                            <Input type="number" step="0.01" min="0.01" {...register("amount", { valueAsNumber: true })} />
                            <p className="text-sm text-red-500">{errors.amount?.message}</p>
                        </Field>

                        <Field>
                            <Label>Data de vencimento</Label>
                            <Input type="date" {...register("dueDate")} />
                            <p className="text-sm text-red-500">{errors.dueDate?.message}</p>
                        </Field>

                        <Field>
                            <Label>Método de pagamento</Label>
                            <Controller
                                control={control}
                                name="paymentMethod"
                                render={({ field }) => (
                                    <Select value={field.value} onValueChange={field.onChange}>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione o método" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="PIX">PIX</SelectItem>
                                            <SelectItem value="CREDIT_CARD">Cartão de Crédito</SelectItem>
                                            <SelectItem value="BOLETO">Boleto</SelectItem>
                                            <SelectItem value="CASH">Dinheiro</SelectItem>
                                        </SelectContent>
                                    </Select>
                                )}
                            />
                            <p className="text-sm text-red-500">{errors.paymentMethod?.message}</p>
                        </Field>
                    </FieldGroup>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button type="button" variant="outline">Cancelar</Button>
                        </DialogClose>
                        <Button type="submit" className="bg-green-500" disabled={isSubmitting}>
                            Registrar
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default PaymentsPage
