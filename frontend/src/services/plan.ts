import { api } from "@/lib/api"
import { PlanFormData } from "@/lib/validations/plan"
import { Plan } from "@/types/plan"

const BASE_URL = "/plans"

export const planService = {

    async list(activeOnly?: boolean): Promise<Plan[]> {
        const response = await api.get(BASE_URL, { params: { activeOnly: activeOnly ?? false } })
        return response.data
    },

    async details(id: number | undefined): Promise<Plan> {
        const response = await api.get(`${BASE_URL}/${id}`)
        return response.data
    },

    async create(data: PlanFormData): Promise<Plan> {
        const response = await api.post(BASE_URL, data)
        return response.data
    },

    async edit(id: number, data: PlanFormData): Promise<Plan> {
        const response = await api.put(`${BASE_URL}/${id}`, data)
        return response.data
    },

    async activate(id: number): Promise<void> {
        await api.patch(`${BASE_URL}/${id}/activate`)
    },

    async deactivate(id: number): Promise<void> {
        await api.patch(`${BASE_URL}/${id}/deactivate`)
    },

    async remove(id: number): Promise<void> {
        await api.delete(`${BASE_URL}/${id}`)
    },
}
