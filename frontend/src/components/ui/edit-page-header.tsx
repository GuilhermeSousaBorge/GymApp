import { Button } from "@/components/ui/button"
import { ArrowLeft, type LucideIcon } from "lucide-react"

interface EditPageHeaderProps {
    icon: LucideIcon
    title: string
    subtitle: string
    onBack: () => void
}

export function EditPageHeader({ icon: Icon, title, subtitle, onBack }: EditPageHeaderProps) {
    return (
        <div className="flex items-center gap-4">
            <Button
                type="button"
                variant="outline"
                size="icon"
                onClick={onBack}
            >
                <ArrowLeft className="h-4 w-4" />
            </Button>
            <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                    <Icon className="h-5 w-5 text-primary" />
                </div>
                <div>
                    <h1 className="text-xl font-bold">{title}</h1>
                    <p className="text-sm text-muted-foreground">{subtitle}</p>
                </div>
            </div>
        </div>
    )
}
