"use client"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Field, FieldGroup } from "@/components/ui/field"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import { Input } from "@/components/ui/input"
import { LoadingState } from "@/components/ui/loading-state"
import { useActiveSubscription, useCancelSubscription, useCreateSubscription, useMySubscription } from "@/hooks/subscription"
import { usePlans } from "@/hooks/plan"
import { useUsers } from "@/hooks/user"
import { handleMessageError } from "@/lib/handle-error"
import { formatDate, isAdmin, isStudent } from "@/lib/utils"
import { SubscriptionFormData, subscriptionSchema } from "@/lib/validations/subscription"
import { useUser } from "@/stores/auth"
import { SubscriptionStatus } from "@/types/subscription"
import { zodResolver } from "@hookform/resolvers/zod"
import { CalendarDays, CreditCard, Search, UserCheck } from "lucide-react"
import Link from "next/link"
import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

const statusLabels: Record<SubscriptionStatus, string> = {
    ACTIVE: "Ativa",
    PAST_DUE: "Em atraso",
    CANCELLED: "Cancelada",
    EXPIRED: "Expirada",
}

const statusColors: Record<SubscriptionStatus, string> = {
    ACTIVE: "bg-green-100 text-green-700 hover:bg-green-100",
    PAST_DUE: "bg-yellow-100 text-yellow-700 hover:bg-yellow-100",
    CANCELLED: "bg-red-100 text-red-700 hover:bg-red-100",
    EXPIRED: "bg-gray-100 text-gray-700 hover:bg-gray-100",
}

const formatCurrency = (value: number) =>
    new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value)

const SubscriptionsPage = () => {
    const user = useUser()
    const isAdminUser = isAdmin(user)
    const isStudentUser = isStudent(user)

    if (isStudentUser) {
        return <StudentSubscriptionView />
    }

    return <AdminSubscriptionView />
}

// === VISÃO DO ALUNO ===
const StudentSubscriptionView = () => {
    const { data: subscription, isLoading, error } = useMySubscription()

    if (isLoading) return <LoadingState message="Carregando inscrição..." />
    const hasSubscription = !error && subscription

    return (
        <div className="flex flex-col gap-6">
            <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                    <CreditCard className="h-5 w-5 text-primary" />
                </div>
                <div>
                    <h1 className="text-xl font-bold">Minha Inscrição</h1>
                    <p className="text-sm text-muted-foreground">Visualize os detalhes da sua inscrição</p>
                </div>
            </div>

            {!hasSubscription ? (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Você não possui uma inscrição ativa no momento.
                    </CardContent>
                </Card>
            ) : (
                <>
                    <SubscriptionCard
                        planName={subscription.planName}
                        status={subscription.status}
                        startDate={subscription.startDate}
                        endDate={subscription.endDate}
                        autoRenew={subscription.autoRenew}
                        price={subscription.planPriceAtStart}
                    />
                    <div className="flex gap-2">
                        <Button asChild variant="outline">
                            <Link href={`/payments?subscriptionId=${subscription.id}`}>
                                Ver pagamentos
                            </Link>
                        </Button>
                    </div>
                </>
            )}
        </div>
    )
}

// === VISÃO DO ADMIN / TRAINER ===
const AdminSubscriptionView = () => {
    const [selectedUserId, setSelectedUserId] = useState<number | undefined>(undefined)
    const [userSearch, setUserSearch] = useState("")
    const [showCreateDialog, setShowCreateDialog] = useState(false)
    const [showCancelDialog, setShowCancelDialog] = useState(false)

    const { data: users = [] } = useUsers()
    const { data: subscription, isLoading: isLoadingSub, error: subError } = useActiveSubscription(selectedUserId)
    const { mutateAsync: cancelSubscription, isPending: isCancelling } = useCancelSubscription()

    const hasSubscription = selectedUserId && !subError && subscription

    const filteredUsers = users.filter(u =>
        u.name.toLowerCase().includes(userSearch.toLowerCase()) ||
        u.email.toLowerCase().includes(userSearch.toLowerCase())
    )

    const handleCancel = async () => {
        if (!subscription) return
        try {
            await cancelSubscription(subscription.id)
            toast.success("Inscrição cancelada com sucesso!", { position: "top-right" })
            setShowCancelDialog(false)
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <div className="flex flex-col gap-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-lg bg-primary/10 flex items-center justify-center">
                        <CreditCard className="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <h1 className="text-xl font-bold">Inscrições</h1>
                        <p className="text-sm text-muted-foreground">Gerencie as inscrições dos alunos</p>
                    </div>
                </div>
                <Button onClick={() => setShowCreateDialog(true)} className="bg-primary text-primary-foreground hover:bg-primary/90">
                    <UserCheck className="h-4 w-4 mr-2" />
                    Nova inscrição
                </Button>
            </div>

            {/* Seletor de aluno */}
            <Card>
                <CardContent className="pt-6">
                    <Field>
                        <Label>Selecionar aluno</Label>
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                            <Input
                                placeholder="Buscar aluno por nome ou email..."
                                className="pl-9 mb-2"
                                value={userSearch}
                                onChange={(e) => setUserSearch(e.target.value)}
                            />
                        </div>
                        {userSearch.length >= 2 && (
                            <div className="border rounded-md max-h-48 overflow-y-auto">
                                {filteredUsers.length === 0 ? (
                                    <p className="p-3 text-sm text-muted-foreground">Nenhum usuário encontrado</p>
                                ) : (
                                    filteredUsers.slice(0, 10).map(u => (
                                        <button
                                            key={u.id}
                                            type="button"
                                            className={`w-full text-left px-3 py-2 hover:bg-muted text-sm ${selectedUserId === u.id ? "bg-primary/10 font-medium" : ""}`}
                                            onClick={() => {
                                                setSelectedUserId(u.id)
                                                setUserSearch(u.name)
                                            }}
                                        >
                                            <span className="font-medium">{u.name}</span>
                                            <span className="text-muted-foreground ml-2">{u.email}</span>
                                        </button>
                                    ))
                                )}
                            </div>
                        )}
                    </Field>
                </CardContent>
            </Card>

            {/* Resultado */}
            {selectedUserId && isLoadingSub && <LoadingState message="Carregando inscrição..." />}

            {selectedUserId && !isLoadingSub && !hasSubscription && (
                <Card>
                    <CardContent className="py-10 text-center text-muted-foreground">
                        Este aluno não possui uma inscrição ativa.
                    </CardContent>
                </Card>
            )}

            {hasSubscription && (
                <>
                    <SubscriptionCard
                        planName={subscription.planName}
                        status={subscription.status}
                        startDate={subscription.startDate}
                        endDate={subscription.endDate}
                        autoRenew={subscription.autoRenew}
                        price={subscription.planPriceAtStart}
                        userName={subscription.userName}
                    />
                    <div className="flex gap-2">
                        <Button asChild variant="outline">
                            <Link href={`/payments?subscriptionId=${subscription.id}`}>
                                Ver pagamentos
                            </Link>
                        </Button>
                        {subscription.status === "ACTIVE" && (
                            <Button
                                variant="destructive"
                                onClick={() => setShowCancelDialog(true)}
                            >
                                Cancelar inscrição
                            </Button>
                        )}
                    </div>
                </>
            )}

            {/* Dialog de confirmação de cancelamento */}
            <Dialog open={showCancelDialog} onOpenChange={setShowCancelDialog}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Cancelar inscrição</DialogTitle>
                    </DialogHeader>
                    <p className="text-sm text-muted-foreground py-4">
                        Tem certeza que deseja cancelar a inscrição de <strong>{subscription?.userName}</strong> no plano <strong>{subscription?.planName}</strong>? Esta ação não pode ser desfeita.
                    </p>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button type="button" variant="outline">Voltar</Button>
                        </DialogClose>
                        <Button variant="destructive" onClick={handleCancel} disabled={isCancelling}>
                            {isCancelling ? "Cancelando..." : "Confirmar cancelamento"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            {/* Dialog de criação de inscrição */}
            <CreateSubscriptionDialog
                open={showCreateDialog}
                onClose={() => setShowCreateDialog(false)}
                preselectedUserId={selectedUserId}
            />
        </div>
    )
}

// === COMPONENTE CARD DA INSCRIÇÃO ===
const SubscriptionCard = ({
    planName,
    status,
    startDate,
    endDate,
    autoRenew,
    price,
    userName,
}: {
    planName: string
    status: SubscriptionStatus
    startDate: string
    endDate: string | null
    autoRenew: boolean
    price: number
    userName?: string
}) => (
    <Card>
        <CardHeader>
            <div className="flex items-center justify-between">
                <CardTitle className="text-base">
                    {userName ? `Inscrição de ${userName}` : "Detalhes da inscrição"}
                </CardTitle>
                <Badge className={statusColors[status]}>
                    {statusLabels[status]}
                </Badge>
            </div>
        </CardHeader>
        <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <div>
                    <p className="text-sm text-muted-foreground">Plano</p>
                    <p className="font-medium">{planName}</p>
                </div>
                <div>
                    <p className="text-sm text-muted-foreground">Valor</p>
                    <p className="font-medium">{formatCurrency(price)}</p>
                </div>
                <div>
                    <p className="text-sm text-muted-foreground">Início</p>
                    <p className="font-medium">{formatDate(new Date(startDate))}</p>
                </div>
                <div>
                    <p className="text-sm text-muted-foreground">Fim</p>
                    <p className="font-medium">{endDate ? formatDate(new Date(endDate)) : "Indeterminado"}</p>
                </div>
                <div>
                    <p className="text-sm text-muted-foreground">Renovação automática</p>
                    <p className="font-medium">{autoRenew ? "Sim" : "Não"}</p>
                </div>
            </div>
        </CardContent>
    </Card>
)

// === DIALOG DE CRIAÇÃO ===
const CreateSubscriptionDialog = ({
    open,
    onClose,
    preselectedUserId,
}: {
    open: boolean
    onClose: () => void
    preselectedUserId?: number
}) => {
    const { data: plans = [] } = usePlans(true)
    const { data: users = [] } = useUsers()
    const { mutateAsync: createSubscription } = useCreateSubscription()

    const {
        register,
        handleSubmit,
        control,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<SubscriptionFormData>({
        resolver: zodResolver(subscriptionSchema),
        values: {
            planId: 0,
            userId: preselectedUserId ?? 0,
            startDate: "",
            endDate: "",
            autoRenew: true,
        }
    })

    const onSubmit = async (data: SubscriptionFormData) => {
        try {
            await createSubscription(data)
            toast.success("Inscrição criada com sucesso!", { position: "top-right" })
            reset()
            onClose()
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
            <DialogContent className="max-w-lg">
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogHeader>
                        <DialogTitle>Nova inscrição</DialogTitle>
                    </DialogHeader>
                    <FieldGroup className="py-4">
                        <Field>
                            <Label>Aluno</Label>
                            <Controller
                                control={control}
                                name="userId"
                                render={({ field }) => (
                                    <Select
                                        value={field.value ? String(field.value) : ""}
                                        onValueChange={(val) => field.onChange(Number(val))}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione um aluno" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {users.map(u => (
                                                <SelectItem key={u.id} value={String(u.id)}>
                                                    {u.name}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                )}
                            />
                            <p className="text-sm text-red-500">{errors.userId?.message}</p>
                        </Field>

                        <Field>
                            <Label>Plano</Label>
                            <Controller
                                control={control}
                                name="planId"
                                render={({ field }) => (
                                    <Select
                                        value={field.value ? String(field.value) : ""}
                                        onValueChange={(val) => field.onChange(Number(val))}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione um plano" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {plans.map(p => (
                                                <SelectItem key={p.id} value={String(p.id)}>
                                                    {p.name} - {formatCurrency(p.price)}
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                )}
                            />
                            <p className="text-sm text-red-500">{errors.planId?.message}</p>
                        </Field>

                        <div className="grid grid-cols-2 gap-4">
                            <Field>
                                <Label>Data de início</Label>
                                <Input type="date" {...register("startDate")} />
                            </Field>
                            <Field>
                                <Label>Data de fim</Label>
                                <Input type="date" {...register("endDate")} />
                            </Field>
                        </div>

                        <Field>
                            <Label>Renovação automática</Label>
                            <Controller
                                control={control}
                                name="autoRenew"
                                render={({ field }) => (
                                    <div className="flex items-center gap-3">
                                        <Switch
                                            checked={field.value}
                                            onCheckedChange={field.onChange}
                                        />
                                        <span className="text-sm text-muted-foreground">
                                            {field.value ? "Sim" : "Não"}
                                        </span>
                                    </div>
                                )}
                            />
                        </Field>
                    </FieldGroup>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button type="button" variant="outline">Cancelar</Button>
                        </DialogClose>
                        <Button type="submit" className="bg-green-500" disabled={isSubmitting}>
                            Criar inscrição
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default SubscriptionsPage
