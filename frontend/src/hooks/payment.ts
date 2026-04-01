import { queryClient } from "@/lib/react-query"
import { PaymentFormData } from "@/lib/validations/payment"
import { paymentService } from "@/services/payment"
import { useMutation, useQuery } from "@tanstack/react-query"

export const usePaymentsBySubscription = (subscriptionId: number | undefined) =>
    useQuery({
        queryKey: ["payments", subscriptionId],
        queryFn: () => paymentService.listBySubscription(subscriptionId!),
        enabled: !!subscriptionId,
    })

export const useCreatePayment = () =>
    useMutation({
        mutationFn: (data: PaymentFormData) => paymentService.create(data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ["payments", variables.subscriptionId] })
        },
    })

export const useMarkPaymentAsPaid = () =>
    useMutation({
        mutationFn: (id: number) => paymentService.markAsPaid(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["payments"] })
        },
    })
