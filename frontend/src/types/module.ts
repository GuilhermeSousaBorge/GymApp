import { LucideIcon } from "lucide-react"
import { Permission } from "./role"

export type Module = {
    key: string
    name: string,
    url?: string
    icon?: LucideIcon
    permission?: Permission[]
    children?: Module[]
}