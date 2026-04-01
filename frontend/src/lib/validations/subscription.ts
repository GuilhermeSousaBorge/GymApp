import z from "zod"

export const subscriptionSchema = z.object({
    planId: z.number().min(1, "Selecione um plano"),
    userId: z.number().min(1, "Selecione um aluno"),
    startDate: z.string().nullish().or(z.literal("")),
    endDate: z.string().nullish().or(z.literal("")),
    autoRenew: z.boolean(),
})

export type SubscriptionFormData = z.infer<typeof subscriptionSchema>
