"use client"

import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { formatDate } from "@/lib/utils";
import { UserFormData } from "@/lib/validations/user";
import { useState } from "react";
import { Controller, useFormContext } from "react-hook-form";

export const UserPersonal = () => {

    const {register, control, formState: {errors}} = useFormContext<UserFormData>()
    const [open, setOpen] = useState(false);


    return (
        <Card>
            <CardHeader>
                <CardTitle>Dados Pessoais</CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">

                <div className="md:col-span-2">
                    <Label>Nome</Label>
                    <Input {...register("name")}/>
                    <p className="text-sm text-red-500">
                        {errors.name?.message?.toString()}
                    </p>
                </div>

                <div className="md:col-span-2">
                    <Label>Email</Label>
                    <Input {...register("email")}/>
                    <p className="text-sm text-red-500">
                        {errors.email?.message?.toString()}
                    </p>
                </div>

                <div>
                    <Label>CPF</Label>
                    <Input {...register("cpf")}/>
                    <p className="text-sm text-red-500">
                        {errors.cpf?.message?.toString()}
                    </p>
                </div>

                <div>
                    <Label>Telefone</Label>
                    <Input {...register("phone")}/>
                    <p className="text-sm text-red-500">
                        {errors.phone?.message?.toString()}
                    </p>
                </div>

                <div>
                    <Controller
                        control={control}
                        name="gender"
                        render={({field}) => {
                                return(
                            <div className="flex flex-col gap-2">
                                <Label>Genero</Label>
                                <Select value={field.value ?? ""} onValueChange={(value) => field.onChange(value)} defaultValue={field.value}>
                                    <SelectTrigger className="w-full">
                                        <SelectValue placeholder="Selecione o genero"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectGroup>
                                            <SelectItem value="MALE">Masculino</SelectItem>
                                            <SelectItem value="FEMALE">Feminino</SelectItem>
                                            <SelectItem value="OTHER">Outro</SelectItem>
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>
                                <p className="text-sm text-red-500">
                                    {errors.gender?.message?.toString()}
                                </p>
                            </div>)
                        }}
                    />
                </div>

                <div>
                    <Controller
                        name="birthDate"
                        control={control}
                        render={({field}) => (
                            <div className="flex flex-col gap-2">
                                <Label>Data de Nascimento</Label>

                                <Popover open={open} onOpenChange={setOpen}>
                                    <PopoverTrigger asChild>
                                        <Button
                                            type="button"
                                            variant="outline"
                                            className="justify-start font-normal w-full"
                                        >
                                            {field.value
                                                ? formatDate(field.value)
                                                : "Selecione uma data"}
                                        </Button>
                                    </PopoverTrigger>

                                    <PopoverContent
                                        className="w-auto overflow-hidden p-0"
                                        align="start"
                                    >
                                        <Calendar
                                            mode="single"
                                            selected={field.value}
                                            onSelect={(date) => {
                                                field.onChange(date)
                                                setOpen(false)
                                            }}
                                            captionLayout="dropdown"
                                        />
                                    </PopoverContent>
                                </Popover>
                            </div>
                        )}
                    />
                    <p className="text-sm text-red-500">
                        {errors.birthDate?.message?.toString()}
                    </p>
                </div>

                <div>
                    <Controller
                        name="active"
                        control={control}
                        render={({field}) => (
                            <div>
                                <Label>Status</Label>

                                <div className="flex items-center gap-3">
                                    <span className="text-red-500">Inativo</span>

                                    <Switch
                                        checked={field.value}
                                        onCheckedChange={field.onChange}
                                    />

                                    <span className="text-green-500">Ativo</span>
                                </div>
                            </div>
                        )}
                    />
                </div>
            </CardContent>
        </Card>
    )
}