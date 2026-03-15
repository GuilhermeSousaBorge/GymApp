import { api } from "@/lib/api";
import { ExerciseFormData } from "@/lib/validations/exercise";
import { Exercise } from "@/types/exercise";

const BASE_URL = "/exercises"

export const exerciseService = {

    async create(exercise: ExerciseFormData): Promise<Exercise> {
        const response = await api.post(BASE_URL, exercise)
        return response.data
    },

    async details(id: number | undefined): Promise<Exercise>{
        const response = await api.get(`${BASE_URL}/${id}`)
        return response.data
    },

    async list(): Promise<Exercise[]>{
        const response = await api.get(BASE_URL)
        return response.data
    },

    async edit(id: number, data: ExerciseFormData): Promise<Exercise>{
        const response = await api.put(`${BASE_URL}/${id}`, data)
        return response.data
    }
}