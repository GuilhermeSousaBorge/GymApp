"use client"

import { Button } from "@/components/ui/button"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Field, FieldGroup } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { handleMessageError } from "@/lib/handle-error"
import { PaymentFormData, paymentSchema } from "@/lib/validations/payment"
import { useCreatePayment } from "@/hooks/payment"
import { zodResolver } from "@hookform/resolvers/zod"
import { Controller, useForm } from "react-hook-form"
import { toast } from "sonner"

interface CreatePaymentDialogProps {
    open: boolean
    onClose: () => void
    subscriptionId: number
}

export function CreatePaymentDialog({ open, onClose, subscriptionId }: CreatePaymentDialogProps) {
    const { mutateAsync: createPayment } = useCreatePayment()

    const {
        register,
        handleSubmit,
        control,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<PaymentFormData>({
        resolver: zodResolver(paymentSchema),
        values: {
            subscriptionId,
            amount: 0,
            dueDate: "",
            paymentMethod: "PIX",
        },
    })

    const onSubmit = async (data: PaymentFormData) => {
        try {
            await createPayment(data)
            toast.success("Pagamento registrado com sucesso!", { position: "top-right" })
            reset()
            onClose()
        } catch (err) {
            const message = handleMessageError(err)
            toast.error(message, { position: "top-right" })
        }
    }

    return (
        <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
            <DialogContent>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <DialogHeader>
                        <DialogTitle>Registrar pagamento</DialogTitle>
                    </DialogHeader>
                    <FieldGroup className="py-4">
                        <Field>
                            <Label>Valor (R$)</Label>
                            <Input type="number" step="0.01" min="0.01" {...register("amount", { valueAsNumber: true })} />
                            <p className="text-sm text-red-500">{errors.amount?.message}</p>
                        </Field>

                        <Field>
                            <Label>Data de vencimento</Label>
                            <Input type="date" {...register("dueDate")} />
                            <p className="text-sm text-red-500">{errors.dueDate?.message}</p>
                        </Field>

                        <Field>
                            <Label>Método de pagamento</Label>
                            <Controller
                                control={control}
                                name="paymentMethod"
                                render={({ field }) => (
                                    <Select value={field.value} onValueChange={field.onChange}>
                                        <SelectTrigger>
                                            <SelectValue placeholder="Selecione o método" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="PIX">PIX</SelectItem>
                                            <SelectItem value="CREDIT_CARD">Cartão de Crédito</SelectItem>
                                            <SelectItem value="BOLETO">Boleto</SelectItem>
                                            <SelectItem value="CASH">Dinheiro</SelectItem>
                                        </SelectContent>
                                    </Select>
                                )}
                            />
                            <p className="text-sm text-red-500">{errors.paymentMethod?.message}</p>
                        </Field>
                    </FieldGroup>
                    <DialogFooter>
                        <DialogClose asChild>
                            <Button type="button" variant="outline">Cancelar</Button>
                        </DialogClose>
                        <Button type="submit" className="bg-green-500" disabled={isSubmitting}>
                            Registrar
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
