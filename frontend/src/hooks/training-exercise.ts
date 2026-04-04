import { queryClient } from "@/lib/react-query";
import { TrainingExerciseFormData } from "@/lib/validations/training-exercise";
import { trainingExerciseService } from "@/services/training-exercise";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

type Params = {
    sheetId: number
}

export const useTrainingExercises = (params: Params) => useQuery({
    queryKey: ["training-exercises", params],
    queryFn: () => trainingExerciseService.list(params),
})

export const useTrainingExerciseDetails = (id: number | undefined) => useQuery({
    queryKey: ["training-exercise", id],
    queryFn: () => trainingExerciseService.details(id),
    enabled: !!id
})

export const useCreateTrainingExericse = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (exercise: TrainingExerciseFormData) => trainingExerciseService.create(exercise),
        onSuccess: (createdTrainingExercise) => {
            queryClient.invalidateQueries({queryKey: ["training-exercises"]})
            router.push(`/training-exercises/${createdTrainingExercise.id}`)
        }
    })
}

export const useUpdateTrainingExercise = () => useMutation({
    mutationFn: ({ id, exercise }: { id: number, exercise: TrainingExerciseFormData }) => trainingExerciseService.edit(id, exercise),
    onSuccess: (updatedTrainingExercise) => queryClient.invalidateQueries({ queryKey: ["training-exercise", updatedTrainingExercise.id] })
})

export const useDeleteTrainingExercise = () => useMutation({
    mutationFn: (id: number) => trainingExerciseService.delete(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["training-exercises"] })
})