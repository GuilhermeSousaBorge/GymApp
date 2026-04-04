import { api } from "@/lib/api"
import { PaymentFormData } from "@/lib/validations/payment"
import { Payment, PaymentStatus } from "@/types/payment"

const BASE_URL = "/payments"

export const paymentService = {

    async create(data: PaymentFormData): Promise<Payment> {
        const response = await api.post(BASE_URL, data)
        return response.data
    },

    async markAsPaid(id: number): Promise<void> {
        await api.patch(`${BASE_URL}/${id}/pay`)
    },

    async listBySubscription(subscriptionId: number): Promise<Payment[]> {
        const response = await api.get(`${BASE_URL}/subscriptions/${subscriptionId}`)
        return response.data
    },

    async listAll(status?: PaymentStatus): Promise<Payment[]> {
        const response = await api.get(BASE_URL, {
            params: status ? { status } : undefined,
        })
        return response.data
    },
}
