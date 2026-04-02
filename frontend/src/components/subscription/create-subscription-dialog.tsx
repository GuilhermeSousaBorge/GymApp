"use client"

import { Button } from "@/components/ui/button"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import { formatCurrency } from "@/lib/format"
import { handleMessageError } from "@/lib/handle-error"
import { SubscriptionFormData, subscriptionSchema } from "@/lib/validations/subscription"
import { useCreateSubscription } from "@/hooks/subscription"
import { usePlans } from "@/hooks/plan"
import { useUsers } from "@/hooks/user"
import { zodResolver } from "@hookform/resolvers/zod"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface CreateSubscriptionDialogProps {
    open: boolean
    onClose: () => void
    preselectedUserId?: number
}

export function CreateSubscriptionDialog({
    open,
    onClose,
    preselectedUserId,
}: CreateSubscriptionDialogProps) {
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
        },
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
                                            {users.map((u) => (
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
                                            {plans.map((p) => (
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
