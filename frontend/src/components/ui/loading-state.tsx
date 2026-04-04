import { Loader2 } from "lucide-react"

type Props = {
    message?: string
}

export const LoadingState = ({ message = "Carregando..." }: Props) => (
    <div className="flex items-center justify-center gap-2 py-10 text-muted-foreground">
        <Loader2 className="h-5 w-5 animate-spin" />
        <span className="text-sm">{message}</span>
    </div>
)
