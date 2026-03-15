import { queryClient } from "@/lib/react-query";
import { ExerciseCategoryFormData } from "@/lib/validations/exericiseCategory";
import { exerciseCategoryService } from "@/services/exerciseCategory";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

export const useExerciseCategories = () => 
    useQuery({
        queryKey: ["exercise-category"],
        queryFn: exerciseCategoryService.list
    })

export const useExerciseCategoryDetails = (id: number | undefined) => 
    useQuery({
        queryKey: ["exercise-category", id],
        queryFn: () => exerciseCategoryService.details(id),
        enabled: !!id
    })

export const useCreateCategory = () => {
    const router = useRouter()
    return useMutation({
        mutationFn: (data: ExerciseCategoryFormData) => exerciseCategoryService.create(data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ["exercise-category"]})
            router.push(`/exercises/categories`)
        } 
    })
}

export const useUpdateCategory = () => useMutation({
    mutationFn: ({id, data}: {id: number, data: ExerciseCategoryFormData}) => exerciseCategoryService.edit(id, data),
    onSuccess: () => queryClient.invalidateQueries({queryKey: ["exercise-category"]}) 
})