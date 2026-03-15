"use client"
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Label} from "@/components/ui/label";
import {User} from "@/types/user";
import {formatDate} from "@/lib/utils";

type Props = {
    user: User | undefined;
}

export const UserSystem = ({ user } : Props) => {

    return (
        <Card>
            <CardHeader>
                <CardTitle>Sistema</CardTitle>
            </CardHeader>
            <CardContent className="grid md:grid-cols-2 gap-4">
                <div className="flex gap-3 md:col-span-2">
                    <Label>ID: </Label>
                    <span>{user?.id}</span>
                </div>
                <div className="flex gap-3 md:col-span-2">
                    <Label>Criado em:</Label>
                    <span>{formatDate(user?.createdAt)}</span>
                </div>
                <div className="flex gap-3 md:col-span-2">
                    <Label>Atualizado em:</Label>
                    <span>{formatDate(user?.updatedAt)}</span>
                </div>
            </CardContent>
        </Card>
    )
}