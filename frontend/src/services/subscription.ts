import { api } from "@/lib/api"
import { SubscriptionFormData } from "@/lib/validations/subscription"
import { Subscription } from "@/types/subscription"

const BASE_URL = "/subscriptions"

export const subscriptionService = {

    async create(data: SubscriptionFormData): Promise<Subscription> {
        const response = await api.post(BASE_URL, data)
        return response.data
    },

    async cancel(id: number): Promise<void> {
        await api.patch(`${BASE_URL}/${id}/cancel`)
    },

    async getActiveByUser(userId: number): Promise<Subscription> {
        const response = await api.get(`${BASE_URL}/users/${userId}/active`)
        return response.data
    },

    async getMyActive(): Promise<Subscription> {
        const response = await api.get(`${BASE_URL}/me/active`)
        return response.data
    },
}
