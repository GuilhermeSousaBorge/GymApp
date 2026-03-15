
import { Weekday, WEEKDAYS } from "@/types/training";
import z from "zod";

export const weekDaysEnum = z.enum(
    Object.keys(WEEKDAYS) as [Weekday, ...Weekday[]]
)

export const trainingSheetSchema = z.object({
    name: z.string().trim().min(1, "O nome não pode ser vazio").max(150, "O nome deve conter no maximo 150 caracteres"),
    description: z.string().trim().min(1, "A descrição não pode ser vazio").max(150, "A descrição deve conter no maximo 150 caracteres"),
    restTimeSeconds: z.number().int().positive().nullish(),
    trainingProgramId: z.number().min(1, "Informe o Programa de treino"),
    active: z.boolean(),
    weekdays: z
        .array(weekDaysEnum)
        .min(1, "Selecione pelo menos um dia")
        .refine(
            (days) => new Set(days).size === days.length,
            "Dias não podem se repetir"
        )
})

export type TrainingSheetFormData = z.infer<typeof trainingSheetSchema>