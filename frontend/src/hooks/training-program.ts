import { queryClient } from "@/lib/react-query";
import { TrainingProgramFormData } from "@/lib/validations/training-program";
import { trainingProgramService } from "@/services/training-program";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

type Params = {
    userId?: number
}

export const usePrograms = (params?: Params) =>
    useQuery({
        queryKey: ["training-programs", params],
        queryFn: () => trainingProgramService.list(params)
    })

export const useProgramDetails = (id: number | undefined) =>
    useQuery({
        queryKey: ["training-program", id],
        queryFn: () => trainingProgramService.details(id),
        enabled: !!id
    })

export const useCreateProgram = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (program: TrainingProgramFormData) => trainingProgramService.create(program),
        onSuccess: (createdProgram) => {
            queryClient.invalidateQueries({ queryKey: ["training-programs"] })
            router.push(`/training-programs/${createdProgram.id}/edit`)
        }
    })
}

export const useUpdateProgram = () => useMutation({
    mutationFn: ({ id, program }: { id: number, program: TrainingProgramFormData }) => trainingProgramService.edit(id, program),
    onSuccess: (updatedProgram) => queryClient.invalidateQueries({ queryKey: ["training-program", updatedProgram.id] })
})

export const useActivateProgram = () => useMutation({
    mutationFn: (id: number) => trainingProgramService.activate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["training-program"] })
})

export const useDeactivateProgram = () => useMutation({
    mutationFn: (id: number) => trainingProgramService.deactivate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["training-program"] })
})

export const useExportProgramToPdf = () => useMutation({
    mutationFn: ({programId, layout}: {programId: number, layout: string}) => trainingProgramService.exportTraining(programId, layout),
    onSuccess: ({ url, filename }) => {
        const link = document.createElement("a")
        link.href = url
        link.download = filename
        link.click()
        URL.revokeObjectURL(url)
    }
})
