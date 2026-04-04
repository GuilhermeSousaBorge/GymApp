"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { formatCurrency } from "@/lib/format"
import { formatDate } from "@/lib/utils"
import { Payment, PaymentMethod, PaymentStatus } from "@/types/payment"
import { CheckCircle } from "lucide-react"

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

interface PaymentsTableProps {
    payments: Payment[]
    showActions: boolean
    onMarkAsPaid?: (id: number) => void
}

export function PaymentsTable({ payments, showActions, onMarkAsPaid }: PaymentsTableProps) {
    return (
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
                            payments.map((payment) => (
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
}
