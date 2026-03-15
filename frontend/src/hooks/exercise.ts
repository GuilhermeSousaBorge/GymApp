import { queryClient } from "@/lib/react-query"
import { ExerciseFormData } from "@/lib/validations/exercise"
import { exerciseService } from "@/services/exercise"
import { useMutation, useQuery } from "@tanstack/react-query"
import { useRouter } from "next/navigation"

export const useExercises = () => 
    useQuery({
        queryKey: ["exercises"],
        queryFn: exerciseService.list
    })

export const useExerciseDetails = (id: number | undefined) => 
    useQuery({
        queryKey: ["exercise", id],
        queryFn: () => exerciseService.details(id),
        enabled: !!id
    })

export const useCreateExercise = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (data: ExerciseFormData) => exerciseService.create(data),
        onSuccess: (createdExercise) => {
            queryClient.invalidateQueries({queryKey: ["exercises"]})
            router.push(`/exercises/${createdExercise.id}/edit`)
        } 
    })
}

export const useUpdateExercise = () => useMutation({
    mutationFn: ({id, data}: {id: number, data: ExerciseFormData}) => exerciseService.edit(id, data),
    onSuccess: (updatedExercise) => queryClient.invalidateQueries({queryKey: ["exercise", updatedExercise.id]}) 
})