import { api } from "@/lib/api";
import { TrainingProgramFormData } from "@/lib/validations/training-program";
import { TrainingProgram } from "@/types/training";

type Params = {
    userId?: number
}

const BASE_URL = "/training-programs"

export const trainingProgramService = {

    async create(program: TrainingProgramFormData): Promise<TrainingProgram> {
        const response = await api.post(BASE_URL, program)
        return response.data
    },

    async list(params?: Params): Promise<TrainingProgram[]> {
        const response = await api.get(BASE_URL, {
            params
        })
        return response.data
    },

    async details(id: number | undefined): Promise<TrainingProgram> {
        const response = await api.get(`${BASE_URL}/${id}`)
        return response.data
    },

    async edit(id: number, program: TrainingProgramFormData): Promise<TrainingProgram> {
        const response = await api.put(`${BASE_URL}/${id}`, program)
        return response.data
    },

    async activate(id: number) {
        const response = await api.patch(`${BASE_URL}/${id}/activate`)
        return response.data
    },

    async deactivate(id: number) {
        const response = await api.patch(`${BASE_URL}/${id}/deactivate`)
        return response.data
    },

    async exportTraining(programId: number) {
        const response = await api.get(`${BASE_URL}/${programId}/export/pdf`, {
            responseType: "blob"
        })
        const disposition = response.headers['content-disposition']
        const filename = disposition?.match(/filename="(.+)"/)?.[1] ?? `programa-${programId}.pdf`
        const blob = new Blob([response.data], { type: "application/pdf" })
        const url = URL.createObjectURL(blob)
        return {url, filename};
    }
}