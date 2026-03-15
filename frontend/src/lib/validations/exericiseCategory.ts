import z from "zod";


export const exerciseCategorySchema = z.object({
    muscleGroup: z.string().min(1, "Grupo muscular não pode ser vazio"),
    description: z.string().max(150, "Maximo de 150 caracteres").optional(),
    active: z.boolean()
})

export type ExerciseCategoryFormData = z.infer<typeof exerciseCategorySchema>