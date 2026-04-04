import { Badge } from "@/components/ui/badge"

interface StatusBadgeProps {
    active: boolean
    activeLabel?: string
    inactiveLabel?: string
}

export function StatusBadge({
    active,
    activeLabel = "Ativo",
    inactiveLabel = "Inativo",
}: StatusBadgeProps) {
    return (
        <Badge
            className={
                active
                    ? "bg-green-100 text-green-700 hover:bg-green-100"
                    : "bg-red-100 text-red-700 hover:bg-red-100"
            }
        >
            {active ? activeLabel : inactiveLabel}
        </Badge>
    )
}
