import { api } from "@/lib/api"
import { TrainingSheetFormData } from "@/lib/validations/training-sheet"
import { TrainingSheet } from "@/types/training"

type ListSheetParams = {
    programId: number
    dayOfWeek?: string
    activeOnly?: boolean
}

const BASE_URL = "/training-sheets"

export const trainingSheetService = {

    async create(sheet: TrainingSheetFormData): Promise<TrainingSheet> {
        const response = await api.post(BASE_URL, sheet)
        return response.data
    },

    async list(params: ListSheetParams): Promise<TrainingSheet[]> {
        const response = await api.get(BASE_URL, {
            params
        })
        return response.data
    },

    async details(sheetId: number | undefined): Promise<TrainingSheet> {
        const response = await api.get(`${BASE_URL}/${sheetId}`)
        return response.data
    },

    async edit(id: number, sheet: TrainingSheetFormData): Promise<TrainingSheet> {
        const response = await api.put(`${BASE_URL}/${id}`, sheet)
        return response.data
    },

    async activate(id: number) {
        const response = await api.patch(`${BASE_URL}/${id}/activate`)
        return response.data
    },

    async deactivate(id: number) {
        const response = await api.patch(`${BASE_URL}/${id}/deactivate`)
        return response.data
    }
}