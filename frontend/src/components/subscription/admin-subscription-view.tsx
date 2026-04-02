"use client"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { PageHeader } from "@/components/ui/page-header"
import { LoadingState } from "@/components/ui/loading-state"
import { UserSearchSelector } from "@/components/ui/user-search-selector"
import { DeleteConfirmDialog } from "@/components/ui/delete-confirm-dialog"
import { SubscriptionCard } from "@/components/subscription/subscription-card"
import { CreateSubscriptionDialog } from "@/components/subscription/create-subscription-dialog"
import { useActiveSubscription, useCancelSubscription } from "@/hooks/subscription"
import { useUsers } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { CreditCard, UserCheck } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { toast } from "sonner"

export function AdminSubscriptionView() {
    const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined)
    const [showCreateDialog, setShowCreateDialog] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)

    const { data: users = [] } = useUsers()
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useActiveSubscription(selectedUserId)
    const { mutateAsync: cancelSubscription, isPending: isCancelling } = useCancelSubscription()

    const hasSubscription = selectedUserId && !subError && subscription

    const handleCancel = async () => {
        if (!subscription) return
        try {
            await cancelSubscription(subscription.id)
            toast.success("Inscrição cancelada com sucesso!", { position: "top-right" })
            setShowCancelDialog(false)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="flex flex-col gap-6">
            <PageHeader
                icon={CreditCard}
                title="Inscrições"
                subtitle="Gerencie as inscrições dos alunos"
                action={{
                    label: "Nova inscrição",
                    icon: UserCheck,
                    onClick: () => setShowCreateDialog(true),
                }}
            />

            <UserSearchSelector
                users={users}
                selectedUserId={selectedUserId}
                onSelect={(id) => setSelectedUserId(id)}
            />

            {selectedUserId && isLoadingSub && <LoadingState message="Carregando inscrição..." />}

            {selectedUserId && !isLoadingSub && !hasSubscription && (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Este aluno não possui uma inscrição ativa.
                    </CardContent>
                </Card>
            )}

            {hasSubscription && (
                <>
                    <SubscriptionCard
                        planName={subscription.planName}
                        status={subscription.status}
                        startDate={subscription.startDate}
                        endDate={subscription.endDate}
                        autoRenew={subscription.autoRenew}
                        price={subscription.planPriceAtStart}
                        userName={subscription.userName}
                    />
                    <div className="flex gap-2">
                        <Button asChild variant="outline">
                            <Link href={`/payments?subscriptionId=${subscription.id}`}>
                                Ver pagamentos
                            </Link>
                        </Button>
                        {subscription.status === "ACTIVE" && (
                            <Button
                                variant="destructive"
                                onClick={() => setShowCancelDialog(true)}
                            >
                                Cancelar inscrição
                            </Button>
                        )}
                    </div>
                </>
            )}

            <DeleteConfirmDialog
                open={showCancelDialog}
                onOpenChange={setShowCancelDialog}
                onConfirm={handleCancel}
                title="Cancelar inscrição"
                description={`Tem certeza que deseja cancelar a inscrição de ${subscription?.userName} no plano ${subscription?.planName}? Esta ação não pode ser desfeita.`}
                isPending={isCancelling}
                confirmLabel="Confirmar cancelamento"
                pendingLabel="Cancelando..."
            />

            <CreateSubscriptionDialog
                open={showCreateDialog}
                onClose={() => setShowCreateDialog(false)}
                preselectedUserId={selectedUserId}
            />
        </div>
    )
}
