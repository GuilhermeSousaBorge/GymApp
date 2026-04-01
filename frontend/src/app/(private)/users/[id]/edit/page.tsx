"use client"

import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { UserAddress } from "@/components/user/user-address"
import { UserPersonal } from "@/components/user/user-personal"
import { UserRole } from "@/components/user/user-role"
import { UserSystem } from "@/components/user/user-system"
import { ErrorState } from "@/components/ui/error-state"
import { LoadingState } from "@/components/ui/loading-state"
import { useCreateUser, useUpdateUser, useUserDetails } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { UserFormData, userFormSchema } from "@/lib/validations/user"
import { zodResolver } from "@hookform/resolvers/zod"
import { ArrowLeft, UserCog } from "lucide-react"
import { useParams, useRouter } from "next/navigation"
import { useEffect } from "react"
import { FormProvider, useForm } from "react-hook-form"
import { toast } from "sonner"

const EditUser = () => {
    const params = useParams()
    const router = useRouter()
    const isEditing = params.id !== "new"

    const { data: user, isLoading, error } = useUserDetails(isEditing ? Number(params.id) : undefined)
    const { mutateAsync: createUser } = useCreateUser()
    const { mutateAsync: updateUser } = useUpdateUser()

    const methods = useForm<UserFormData>({
        resolver: zodResolver(userFormSchema),
    })

    useEffect(() => {
        if (!user) return
        methods.reset({
            name: user.name,
            email: user.email,
            cpf: user.cpf ?? "",
            phone: user.phone ?? "",
            gender: user.gender,
            active: user.active,
            roleId: user.role?.id,
            address: {
                zipCode: user.address?.zipCode ?? "",
                streetName: user.address?.streetName ?? "",
                number: user.address?.number ?? 0,
                district: user.address?.district ?? "",
                city: user.address?.city ?? "",
                state: user.address?.state ?? "",
            },
            birthDate: user.birthDate ? new Date(user.birthDate) : undefined,
        })
    }, [methods, user])

    if (isEditing && isLoading) return <LoadingState message="Carregando detalhes do usuário..." />
    if (isEditing && error) return <ErrorState message="Erro ao carregar dados do usuário" />
    if (isEditing && !user) return <ErrorState message="Usuário não encontrado" />

    const onSubmit = async (data: UserFormData) => {
        try {
            if (isEditing) {
                await updateUser({ id: Number(params.id), data })
                toast.success("Usuário atualizado com sucesso", { position: "top-right" })
            } else {
                await createUser(data)
                toast.success("Usuário criado com sucesso", { position: "top-right" })
            }
            router.push("/users")
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="flex flex-col gap-6">

            {/* Header */}
            <div className="flex items-center gap-4">
                <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={() => router.push("/users")}
                >
                    <ArrowLeft className="h-4 w-4" />
                </Button>
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <UserCog className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">
                            {isEditing ? user?.name : "Novo usuário"}
                        </h1>
                        <p className="text-sm text-muted-foreground">
                            {isEditing ? "Edite as informações do usuário" : "Preencha as informações do novo usuário"}
                        </p>
                    </div>
                </div>
            </div>

            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(onSubmit)} className="flex flex-col gap-4">
                    <Tabs defaultValue="personal" className="w-full">
                        <TabsList>
                            <TabsTrigger value="personal">Dados pessoais</TabsTrigger>
                            <TabsTrigger value="role">Cargo</TabsTrigger>
                            <TabsTrigger value="address">Endereço</TabsTrigger>
                            {isEditing && <TabsTrigger value="system">Sistema</TabsTrigger>}
                        </TabsList>

                        <TabsContent value="personal" className="mt-4">
                            <UserPersonal />
                        </TabsContent>

                        <TabsContent value="role" className="mt-4">
                            <UserRole user={user} />
                        </TabsContent>

                        <TabsContent value="address" className="mt-4">
                            <UserAddress />
                        </TabsContent>

                        {isEditing && (
                            <TabsContent value="system" className="mt-4">
                                <UserSystem user={user} />
                            </TabsContent>
                        )}
                    </Tabs>

                    <div className="flex justify-end gap-2">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => router.push("/users")}
                        >
                            Cancelar
                        </Button>
                        <Button type="submit" className="bg-green-500">
                            {isEditing ? "Salvar alterações" : "Criar usuário"}
                        </Button>
                    </div>
                </form>
            </FormProvider>
        </div>
    )
}

export default EditUser