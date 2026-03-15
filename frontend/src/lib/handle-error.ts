import { AxiosError } from "axios"
import { notFound, redirect } from "next/navigation"


export const handleApiError = (error: unknown) => {
    if(error instanceof AxiosError){
        const status = error.response?.status
        if(status == 403) redirect("/unauthorized") 
        if(status == 404) notFound()
    }
}

export const handleMessageError = (err: unknown) => {
    return err instanceof AxiosError
        ? err.response?.data?.message ?? "Erro inesperado"
        : "Algo deu errado"
}