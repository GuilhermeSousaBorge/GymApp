import { api } from "@/lib/api";
import { ExerciseCategoryFormData } from "@/lib/validations/exericiseCategory";
import { ExerciseCategory } from "@/types/exercise-category";

const BASE_URL = "/exercise-categories"

export const exerciseCategoryService = {

    async create(category: ExerciseCategoryFormData): Promise<ExerciseCategory> {
        const response = await api.post(BASE_URL, category);
        return response.data
    },

    async details(id: number | undefined):Promise<ExerciseCategory> {
        const response = await api.get(`${BASE_URL}/${id}`);
        return response.data
    },

    async list(): Promise<ExerciseCategory[]> {
        const response = await api.get(BASE_URL);
        return response.data
    },

    async edit(id: number, data: ExerciseCategoryFormData): Promise<ExerciseCategory> {
        const response = await api.put(`${BASE_URL}/${id}`, data);
        return response.data
    }
}