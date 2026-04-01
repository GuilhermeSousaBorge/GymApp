import { queryClient } from "@/lib/react-query"
import { PlanFormData } from "@/lib/validations/plan"
import { planService } from "@/services/plan"
import { useMutation, useQuery } from "@tanstack/react-query"
import { useRouter } from "next/navigation"

export const usePlans = (activeOnly?: boolean) =>
    useQuery({
        queryKey: ["plans", activeOnly],
        queryFn: () => planService.list(activeOnly),
    })

export const usePlanDetails = (id: number | undefined) =>
    useQuery({
        queryKey: ["plan", id],
        queryFn: () => planService.details(id),
        enabled: !!id,
    })

export const useCreatePlan = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (data: PlanFormData) => planService.create(data),
        onSuccess: (createdPlan) => {
            queryClient.invalidateQueries({ queryKey: ["plans"] })
            router.push(`/plans/${createdPlan.id}/edit`)
        },
    })
}

export const useUpdatePlan = () =>
    useMutation({
        mutationFn: ({ id, data }: { id: number; data: PlanFormData }) => planService.edit(id, data),
        onSuccess: (updatedPlan) => {
            queryClient.invalidateQueries({ queryKey: ["plan", updatedPlan.id] })
            queryClient.invalidateQueries({ queryKey: ["plans"] })
        },
    })

export const useActivatePlan = () =>
    useMutation({
        mutationFn: (id: number) => planService.activate(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["plans"] })
        },
    })

export const useDeactivatePlan = () =>
    useMutation({
        mutationFn: (id: number) => planService.deactivate(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["plans"] })
        },
    })

export const useDeletePlan = () =>
    useMutation({
        mutationFn: (id: number) => planService.remove(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["plans"] })
        },
    })
