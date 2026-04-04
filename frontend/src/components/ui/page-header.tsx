import { Button } from "@/components/ui/button"
import { type LucideIcon } from "lucide-react"
import Link from "next/link"

interface PageHeaderAction {
    label: string
    href?: string
    onClick?: () => void
    icon?: LucideIcon
}

interface PageHeaderProps {
    icon: LucideIcon
    title: string
    subtitle: string
    action?: PageHeaderAction
}

export function PageHeader({ icon: Icon, title, subtitle, action }: PageHeaderProps) {
    return (
        <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                    <Icon className="h-5 w-5 text-primary" />
                </div>
                <div>
                    <h1 className="text-xl font-bold">{title}</h1>
                    <p className="text-sm text-muted-foreground">{subtitle}</p>
                </div>
            </div>
            {action && (
                action.href ? (
                    <Button asChild>
                        <Link href={action.href}>
                            {action.icon && <action.icon className="h-4 w-4 mr-2" />}
                            {action.label}
                        </Link>
                    </Button>
                ) : (
                    <Button onClick={action.onClick}>
                        {action.icon && <action.icon className="h-4 w-4 mr-2" />}
                        {action.label}
                    </Button>
                )
            )}
        </div>
    )
}
