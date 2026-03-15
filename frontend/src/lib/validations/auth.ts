
import { Gender } from '@/types/user'
import {z} from 'zod'

export const loginSchema = z.object({
    email: z.email("Email inválido"),
    password: z.string().min(6, "A senha deve conter no minímo 6 caracteres")
})

export const registerSchema = z.object({
    name: z.string().min(2, "Nome muito curto"),
    email: z.email("Email inválido"),
    gender: z.string<Gender>("Selecione o genero"),
    password: z.string().min(6, "A senha deve conter no minímo 6 caracteres"),
    confirmPassword: z.string()
}).refine((data) => data.password === data.confirmPassword, {
    error: "As senhas não são iguais",
    path: ['confirmPassword']
})