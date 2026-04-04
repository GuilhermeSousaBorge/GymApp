export type PaymentStatus = "PENDING" | "PAID" | "FAILED" | "CANCELLED" | "REFUNDED"

export type PaymentMethod = "PIX" | "CREDIT_CARD" | "BOLETO" | "CASH"

export type Payment = {
    id: number
    subscriptionId: number
    status: PaymentStatus
    amount: number
    dueDate: string
    paymentDate: string | null
    paymentMethod: PaymentMethod
    createdAt: string
    updatedAt: string
}
