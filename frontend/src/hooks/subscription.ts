import { queryClient } from "@/lib/react-query"
import { SubscriptionFormData } from "@/lib/validations/subscription"
import { subscriptionService } from "@/services/subscription"
import { SubscriptionStatus } from "@/types/subscription"
import { useMutation, useQuery } from "@tanstack/react-query"

export const useSubscriptions = (status?: SubscriptionStatus) =>
    useQuery({
        queryKey: ["subscriptions", status],
        queryFn: () => subscriptionService.listAll(status),
    })

export const useActiveSubscription = (userId: number | undefined) =>
    useQuery({
        queryKey: ["subscription", "active", userId],
        queryFn: () => subscriptionService.getActiveByUser(userId!),
        enabled: !!userId,
        retry: false,
    })

export const useMySubscription = () =>
    useQuery({
        queryKey: ["subscription", "me"],
        queryFn: () => subscriptionService.getMyActive(),
        retry: false,
    })

export const useCreateSubscription = () =>
    useMutation({
        mutationFn: (data: SubscriptionFormData) => subscriptionService.create(data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["subscription"] })
            queryClient.invalidateQueries({ queryKey: ["subscriptions"] })
        },
    })

export const useCancelSubscription = () =>
    useMutation({
        mutationFn: (id: number) => subscriptionService.cancel(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["subscription"] })
            queryClient.invalidateQueries({ queryKey: ["subscriptions"] })
        },
    })
