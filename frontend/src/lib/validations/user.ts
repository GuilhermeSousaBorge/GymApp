import {z} from "zod";
import {isValidCPF} from "@/lib/utils";


export const userFormSchema = z.object({
    name: z.string().min(3, "Nome muito curto"),
    email: z.email("Email inválido"),
    cpf: z
        .string()
        .regex(/^\d{11}$/, "CPF deve conter 11 números")
        .refine((value) => isValidCPF(value), {
            message: "CPF inválido",
        }),
    phone: z.string().regex(/^\d{10,11}$/, "Telefone inválido"),
    gender: z.enum(["MALE", "FEMALE", "OTHER"], "Genero invalido"),
    active: z.boolean(),
    roleId: z.number(),
    birthDate: z
        .date({ error: "Data obrigatória" })
        .max(new Date(), "Data inválida"),
    address: z.object({
        zipCode: z.string().regex(/^\d{8}$/, "CEP inválido"),
        streetName: z.string().min(3, "Digite o nome da rua"),
        number: z.number().min(1, "Informe o numero da casa"),
        district: z.string().min(2, "Digite o bairro"),
        city: z.string().min(2, "Digite a cidade"),
        state: z.string().length(2, "Selecione o estado"),
    }),
})

export type UserFormData = z.infer<typeof userFormSchema>