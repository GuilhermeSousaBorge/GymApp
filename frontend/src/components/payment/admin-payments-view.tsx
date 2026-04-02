"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { PageHeader } from "@/components/ui/page-header"
import { LoadingState } from "@/components/ui/loading-state"
import { UserSearchSelector } from "@/components/ui/user-search-selector"
import { PaymentsTable } from "@/components/payment/payments-table"
import { CreatePaymentDialog } from "@/components/payment/create-payment-dialog"
import { usePaymentsBySubscription, useMarkPaymentAsPaid } from "@/hooks/payment"
import { useActiveSubscription } from "@/hooks/subscription"
import { useUsers } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { Banknote, Plus } from "lucide-react"
import { useSearchParams } from "next/navigation"
import { useState } from "react"
import { toast } from "sonner"

export function AdminPaymentsView() {
    const searchParams = useSearchParams()
    const preselectedSubId = searchParams.get("subscriptionId")

    const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined)
    const [showCreateDialog, setShowCreateDialog] = useState(false)

    const { data: users = [] } = useUsers()
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useActiveSubscription(selectedUserId)

    const effectiveSubscriptionId = preselectedSubId ? Number(preselectedSubId) : subscription?.id
    const { data: payments = [], isLoading: isLoadingPayments } = usePaymentsBySubscription(effectiveSubscriptionId)
    const { mutateAsync: markAsPaid } = useMarkPaymentAsPaid()

    const hasSubscription = (preselectedSubId && effectiveSubscriptionId) || (selectedUserId && !subError && subscription)

    const handleMarkAsPaid = async (paymentId: number) => {
        try {
            await markAsPaid(paymentId)
            toast.success("Pagamento marcado como pago!", { position: "top-right" })
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={Banknote}
                title="Pagamentos"
                subtitle="Gerencie os pagamentos dos alunos"
                action={hasSubscription ? {
                    label: "Registrar pagamento",
                    icon: Plus,
                    onClick: () => setShowCreateDialog(true),
                } : undefined}
            />

            {!preselectedSubId && (
                <UserSearchSelector
                    users={users}
                    selectedUserId={selectedUserId}
                    onSelect={(id) => setSelectedUserId(id)}
                />
            )}

            {selectedUserId && isLoadingSub && <LoadingState message="Carregando inscrição..." />}

            {selectedUserId && !isLoadingSub && !hasSubscription && (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Este aluno não possui uma inscrição ativa. Não há pagamentos para exibir.
                    </CardContent>
                </Card>
            )}

            {hasSubscription && (
                isLoadingPayments ? (
                    <LoadingState message="Carregando pagamentos..." />
                ) : (
                    <PaymentsTable
                        payments={payments}
                        showActions={true}
                        onMarkAsPaid={handleMarkAsPaid}
                    />
                )
            )}

            {effectiveSubscriptionId && (
                <CreatePaymentDialog
                    open={showCreateDialog}
                    onClose={() => setShowCreateDialog(false)}
                    subscriptionId={effectiveSubscriptionId}
                />
            )}
        </div>
    )
}
