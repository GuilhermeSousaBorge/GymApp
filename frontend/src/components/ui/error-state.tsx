import { AlertCircle } from "lucide-react"

type Props = {
    message?: string
}

export const ErrorState = ({ message = "Erro ao carregar dados" }: Props) => (
    <div className="flex items-center justify-center gap-2 py-10 text-destructive">
        <AlertCircle className="h-5 w-5" />
        <span className="text-sm">{message}</span>
    </div>
)
