import z from "zod";



export const trainingExerciseSchema = z.object({
    reps: z.string().trim().min(1, "Informe as repetições").regex(/^\d+(-\d+)*$/, "Formato deve ser ex: 8-10 ou 10-8-6"),
    sets: z.number().int().positive().min(1, "Numero minimo de series: 1"),
    restTimeInSeconds: z.number().int().positive().nullish(),
    techniqueNotes: z.string().trim().nullish(),
    exerciseId: z.number().min(1, "Informe o exercicio"),
    trainingSheetId: z.number().min(1, "Informe a ficha de treino")
})

export type TrainingExerciseFormData = z.infer<typeof trainingExerciseSchema>