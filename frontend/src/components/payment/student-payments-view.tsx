"use client"

import { Card, CardContent } from "@/components/ui/card"
import { PageHeader } from "@/components/ui/page-header"
import { LoadingState } from "@/components/ui/loading-state"
import { PaymentsTable } from "@/components/payment/payments-table"
import { usePaymentsBySubscription } from "@/hooks/payment"
import { useMySubscription } from "@/hooks/subscription"
import { Banknote } from "lucide-react"

export function StudentPaymentsView() {
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useMySubscription()
    const subscriptionId = subscription?.id
    const { data: payments = [], isLoading: isLoadingPayments } = usePaymentsBySubscription(subscriptionId)

    if (isLoadingSub) return <LoadingState message="Carregando dados..." />
    const hasSubscription = !subError && subscription

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Banknote}
                title="Meus Pagamentos"
                subtitle={
                    hasSubscription
                        ? `Pagamentos do plano ${subscription.planName}`
                        : "Visualize seus pagamentos"
                }
            />

            {!hasSubscription ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Você não possui uma inscrição ativa. Não há pagamentos para exibir.
                    </CardContent>
                </Card>
            ) : isLoadingPayments ? (
                <LoadingState message="Carregando pagamentos..." />
            ) : (
                <PaymentsTable payments={payments} showActions={false} />
            )}
        </div>
    )
}
