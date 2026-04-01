export type SubscriptionStatus = "ACTIVE" | "PAST_DUE" | "CANCELLED" | "EXPIRED"

export type Subscription = {
    id: number
    planId: number
    planName: string
    userId: number
    userName: string
    startDate: string
    endDate: string | null
    status: SubscriptionStatus
    cancelledAt: string | null
    autoRenew: boolean
    planPriceAtStart: number
    createdAt: string
    updatedAt: string
}
