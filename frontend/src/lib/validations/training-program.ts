import z from "zod";


export const trainingProgramSchema = z.object({
    name: z.string().trim().min(1, "O nome não pode ser vazio").max(150, "O nome deve conter ate 150 caracteres"),
    description: z.string().trim().min(1, "A descrição nao pode ser vazia").max(150, "A descrição deve conter ate 150 caracteres"),
    userId: z.number().min(1, "Informe o aluno"),
    trainerId: z.number().nullish(),
    active: z.boolean()
})

export type TrainingProgramFormData = z.infer<typeof trainingProgramSchema>