import { api } from "@/lib/api";
import { TrainingExerciseFormData } from "@/lib/validations/training-exercise";
import { TrainingExercise } from "@/types/training";

type Params = {
    sheetId: number
}

const BASE_URL = "/training-exercises"

export const trainingExerciseService = {

    async create(exercise: TrainingExerciseFormData): Promise<TrainingExercise> {
        const response = await api.post(BASE_URL, exercise)
        return response.data
    },

    async list(params: Params): Promise<TrainingExercise[]> {
        const response = await api.get(`${BASE_URL}`, {
            params
        })
        return response.data
    },

    async details(exerciseId: number | undefined): Promise<TrainingExercise> {
        const response = await api.get(`${BASE_URL}/${exerciseId}`)
        return response.data
    },

    async edit(id: number, exercise: TrainingExerciseFormData): Promise<TrainingExercise> {
        const response = await api.put(`${BASE_URL}/${id}`, exercise)
        return response.data
    },

    async delete(id: number) {
        const response = await api.delete(`${BASE_URL}/${id}`)
        return response.data
    }
}