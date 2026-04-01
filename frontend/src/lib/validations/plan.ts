import z from "zod"

export const planSchema = z.object({
    name: z.string().min(1, "Nome do plano é obrigatório").max(100, "Máximo de 100 caracteres"),
    description: z.string().max(1000, "Máximo de 1000 caracteres").nullish().or(z.literal("")),
    price: z.number().min(0, "O preço não pode ser negativo"),
    maxStudents: z.number().min(0, "O limite de alunos não pode ser negativo"),
    maxPrograms: z.number().min(1, "O limite de programas deve ser pelo menos 1"),
    benefits: z.array(z.string()),
    active: z.boolean(),
})

export type PlanFormData = z.infer<typeof planSchema>
