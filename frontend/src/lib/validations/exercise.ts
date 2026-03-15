import z from "zod";

export const exerciseSchema = z.object({
    name: z.string().min(1, "Nome do exercicio não pode ser vazio"),
    equipment: z.string().nullish().or(z.literal("")), // Aceita null, undefined ou ""
    videoUrl: z.string().nullish().or(z.literal("")),
    description: z.string().max(150, "Maximo de 150 caracteres").nullish().or(z.literal("")),
    active: z.boolean(),
    categoryId: z.number().min(1, "Informe a categoria do exercício")
})

export type ExerciseFormData = z.infer<typeof exerciseSchema>
