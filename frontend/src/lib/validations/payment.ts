import z from "zod"

export const paymentSchema = z.object({
    subscriptionId: z.number().min(1, "A assinatura é obrigatória"),
    amount: z.number().positive("O valor deve ser maior que zero"),
    dueDate: z.string().min(1, "A data de vencimento é obrigatória"),
    paymentMethod: z.enum(["PIX", "CREDIT_CARD", "BOLETO", "CASH"], {
        message: "Selecione um método de pagamento",
    }),
})

export type PaymentFormData = z.infer<typeof paymentSchema>
