import { queryClient } from "@/lib/react-query";
import { UserFormData } from "@/lib/validations/user";
import { userService } from "@/services/user";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

export const useUsers = () =>
    useQuery({
        queryKey: ["users"],
        queryFn: () => userService.listUsers(),
    });

export const useUserDetails = (id: number | undefined) =>
    useQuery({
        queryKey: ["users", id],
        queryFn: () => userService.details(id),
        enabled: !!id
    })

export const useCreateUser = () => {
    const router = useRouter();
    return useMutation({
        mutationFn: (data: UserFormData) => userService.createUser(data),
        onSuccess: (createdUser) => {
            queryClient.invalidateQueries({queryKey: ["users"]})
            router.push(`/users/${createdUser.id}/edit`);
        }
    })
}

export const useUpdateUser = () => useMutation({
    mutationFn: ({id, data}: {id: number, data: UserFormData}) => userService.updateUser(id, data),
    onSuccess: (updatedUser) => queryClient.invalidateQueries({queryKey: ["users", updatedUser.id]}),
})