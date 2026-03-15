"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { isAdmin } from "@/lib/utils";
import { User } from "@/types/user";
import { Controller, useFormContext } from "react-hook-form";
import { Input } from "../ui/input";


type Props = {
    user: User | undefined;
}

export const UserRole = ({ user }: Props) => {

    const { control } = useFormContext();
    const isAdminUser = isAdmin(user!)

    return (
        <Card>
            <CardHeader>
                <CardTitle>Cargo</CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
                {isAdminUser ? <Controller
                    name="roleId"
                    control={control}
                    render={({ field }) => (
                        <div className="md:col-span-2">
                            <Select value={String(field.value)} onValueChange={(value) => field.onChange(Number(value))}>
                                <SelectTrigger className="w-full">
                                    <SelectValue placeholder="Selecione o cargo" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectGroup>
                                        <SelectItem value="1">Administrador</SelectItem>
                                        <SelectItem value="2">Personal trainer</SelectItem>
                                        <SelectItem value="3">Recepcionista</SelectItem>
                                        <SelectItem value="4">Aluno</SelectItem>
                                    </SelectGroup>
                                </SelectContent>
                            </Select>
                        </div>
                    )}
                /> :
                    <Input disabled value={user?.role?.name} />
                }
                <div className="flex gap-2 md:col-span-2">
                    <strong>Descrição:</strong>
                    <span>{user?.role?.description}</span>
                </div>
            </CardContent>
        </Card>
    )
}