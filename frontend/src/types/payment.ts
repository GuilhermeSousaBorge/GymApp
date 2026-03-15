export type PaymentStatus = "PENDING" | "PAID" | "OVERDUE"

export type PaymentMethod = "CREDIT_CARD" | "PIX" | "CASH" | "TRANSFER"

export type Payment = {
  id: number
  month: number
  year: number
  status: PaymentStatus
  amount: number

  dueDate: string
  paymentDate?: string

  userId: number
  planId: number
  paymentMethod: PaymentMethod
}