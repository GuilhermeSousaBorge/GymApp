"use client"

import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { formatCurrency } from "@/lib/format"
import { formatDate } from "@/lib/utils"
import { SubscriptionStatus } from "@/types/subscription"

const statusLabels: Record<SubscriptionStatus, string> = {
    ACTIVE: "Ativa",
    PAST_DUE: "Em atraso",
    CANCELLED: "Cancelada",
    EXPIRED: "Expirada",
}

const statusColors: Record<SubscriptionStatus, string> = {
    ACTIVE: "bg-green-100 text-green-700 hover:bg-green-100",
    PAST_DUE: "bg-yellow-100 text-yellow-700 hover:bg-yellow-100",
    CANCELLED: "bg-red-100 text-red-700 hover:bg-red-100",
    EXPIRED: "bg-gray-100 text-gray-700 hover:bg-gray-100",
}

interface SubscriptionCardProps {
    planName: string
    status: SubscriptionStatus
    startDate: string
    endDate: string | null
    autoRenew: boolean
    price: number
    userName?: string
}

export function SubscriptionCard({
    planName,
    status,
    startDate,
    endDate,
    autoRenew,
    price,
    userName,
}: SubscriptionCardProps) {
    return (
        <Card>
            <CardHeader>
                <div className="flex items-center justify-between">
                    <CardTitle className="text-base">
                        {userName ? `Inscrição de ${userName}` : "Detalhes da inscrição"}
                    </CardTitle>
                    <Badge className={statusColors[status]}>
                        {statusLabels[status]}
                    </Badge>
                </div>
            </CardHeader>
            <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                    <div>
                        <p className="text-sm text-muted-foreground">Plano</p>
                        <p className="font-medium">{planName}</p>
                    </div>
                    <div>
                        <p className="text-sm text-muted-foreground">Valor</p>
                        <p className="font-medium">{formatCurrency(price)}</p>
                    </div>
                    <div>
                        <p className="text-sm text-muted-foreground">Início</p>
                        <p className="font-medium">{formatDate(new Date(startDate))}</p>
                    </div>
                    <div>
                        <p className="text-sm text-muted-foreground">Fim</p>
                        <p className="font-medium">{endDate ? formatDate(new Date(endDate)) : "Indeterminado"}</p>
                    </div>
                    <div>
                        <p className="text-sm text-muted-foreground">Renovação automática</p>
                        <p className="font-medium">{autoRenew ? "Sim" : "Não"}</p>
                    </div>
                </div>
            </CardContent>
        </Card>
    )
}
