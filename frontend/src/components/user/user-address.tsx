"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { UserFormData } from "@/lib/validations/user";
import { Controller, useFormContext } from "react-hook-form";

export const UserAddress = () => {

    const {register, control, formState: {errors} } = useFormContext<UserFormData>();
    return (
        <Card>
            <CardHeader>
                <CardTitle>Endereço</CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">

                <div className="md:col-span-2">
                    <Label>CEP</Label>
                    <Input {...register("address.zipCode")}/>
                    {errors.address?.zipCode && (
                        <p className="text-sm text-red-500">
                            {errors.address.zipCode.message}
                        </p>
                    )}
                </div>

                <div className="md:col-span-2">
                    <Label>Logradouro</Label>
                    <Input {...register("address.streetName")}/>
                    {errors.address?.streetName && (
                        <p className="text-sm text-red-500">
                            {errors.address.streetName.message}
                        </p>
                    )}
                </div>

                <div>
                    <Label>Número</Label>
                    <Input type="number" {...register("address.number")}/>
                    {errors.address?.number && (
                        <p className="text-sm text-red-500">
                            {errors.address.number.message}
                        </p>
                    )}
                </div>

                <div>
                    <Label>Bairro</Label>
                    <Input {...register("address.district")}/>
                    {errors.address?.district && (
                        <p className="text-sm text-red-500">
                            {errors.address.district.message}
                        </p>
                    )}
                </div>

                <div>
                    <Label>Cidade</Label>
                    <Input {...register("address.city")}/>
                    {errors.address?.city && (
                        <p className="text-sm text-red-500">
                            {errors.address.city.message}
                        </p>
                    )}
                </div>

                <div>
                    <Controller
                        name="address.state"
                        control={control}
                        render={({field}) => (
                            <><Label>Estado</Label>
                                <Select
                                    value={field.value}
                                    onValueChange={field.onChange}
                                >
                                    <SelectTrigger className="w-full">
                                        <SelectValue placeholder="Selecione o estado"/>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectGroup>
                                            <SelectItem value="MG">Minas Gerais</SelectItem>
                                            <SelectItem value="TS">estado teste</SelectItem>
                                        </SelectGroup>
                                    </SelectContent>
                                </Select>
                            </>
                        )}
                    />
                    {errors.address?.state && (
                        <p className="text-sm text-red-500">
                            {errors.address.state.message}
                        </p>
                    )}
                </div>
            </CardContent>
        </Card>
    )
}


