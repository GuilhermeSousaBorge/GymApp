"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { PageHeader } from "@/components/ui/page-header"
import { LoadingState } from "@/components/ui/loading-state"
import { SubscriptionCard } from "@/components/subscription/subscription-card"
import { useMySubscription } from "@/hooks/subscription"
import { CreditCard } from "lucide-react"
import Link from "next/link"

export function StudentSubscriptionView() {
    const { data: subscription, isLoading, error } = useMySubscription()

    if (isLoading) return <LoadingState message="Carregando inscrição..." />
    const hasSubscription = !error && subscription

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={CreditCard}
                title="Minha Inscrição"
                subtitle="Visualize os detalhes da sua inscrição"
            />

            {!hasSubscription ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Você não possui uma inscrição ativa no momento.
                    </CardContent>
                </Card>
            ) : (
                <>
                    <SubscriptionCard
                        planName={subscription.planName}
                        status={subscription.status}
                        startDate={subscription.startDate}
                        endDate={subscription.endDate}
                        autoRenew={subscription.autoRenew}
                        price={subscription.planPriceAtStart}
                    />
                    <div className="flex gap-2">
                        <Button asChild variant="outline">
                            <Link href={`/payments?subscriptionId=${subscription.id}`}>
                                Ver pagamentos
                            </Link>
                        </Button>
                    </div>
                </>
            )}
        </div>
    )
}
