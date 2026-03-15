import { queryClient } from "@/lib/react-query";
import { TrainingSheetFormData } from "@/lib/validations/training-sheet";
import { trainingSheetService } from "@/services/training-sheet";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

type Params = {
    programId: number
    dayOfWeek?: string
    activeOnly?: boolean
}

export const useSheets = (params: Params) =>
    useQuery({
        queryKey: ["training-sheets", params],
        queryFn: () => trainingSheetService.list(params)
    })

export const useSheetDetails = (id: number | undefined) =>
    useQuery({
        queryKey: ["training-sheet", id],
        queryFn: () => trainingSheetService.details(id)
    })

export const useCreateSheet = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (sheet: TrainingSheetFormData) => trainingSheetService.create(sheet),
        onSuccess: (createdSheet) => {
            queryClient.invalidateQueries({ queryKey: ["training-sheets"] })
            router.push(`/training-sheet/${createdSheet.id}/edit`)
        }
    })
}

export const useUpdateSheet = () => useMutation({
    mutationFn: ({ id, sheet }: { id: number, sheet: TrainingSheetFormData }) => trainingSheetService.edit(id, sheet),
    onSuccess: (updatedProgram) => queryClient.invalidateQueries({ queryKey: ["training-sheet", updatedProgram.id] })
})

export const useActivateSheet = () => useMutation({
    mutationFn: (id: number) => trainingSheetService.activate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["training-sheet"] })
})

export const useDeactivateSheet = () => useMutation({
    mutationFn: (id: number) => trainingSheetService.deactivate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["training-sheet"] })
})