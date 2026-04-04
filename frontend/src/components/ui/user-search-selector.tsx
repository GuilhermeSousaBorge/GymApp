"use client"

import { Field } from "@/components/ui/field"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent } from "@/components/ui/card"
import { Search } from "lucide-react"
import { useState } from "react"

interface UserOption {
    id: number
    name: string
    email: string
}

interface UserSearchSelectorProps {
    users: UserOption[]
    selectedUserId?: number
    onSelect: (userId: number, userName: string) => void
    label?: string
    placeholder?: string
}

export function UserSearchSelector({
    users,
    selectedUserId,
    onSelect,
    label = "Selecionar aluno",
    placeholder = "Buscar aluno por nome ou email...",
}: UserSearchSelectorProps) {
    const [search, setSearch] = useState("")

    const filtered = users.filter(
        (u) =>
            u.name.toLowerCase().includes(search.toLowerCase()) ||
            u.email.toLowerCase().includes(search.toLowerCase())
    )

    return (
        <Card>
            <CardContent className="pt-6">
                <Field>
                    <Label>{label}</Label>
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder={placeholder}
                            className="pl-9 mb-2"
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                        />
                    </div>
                    {search.length >= 2 && (
                        <div className="border rounded-md max-h-48 overflow-y-auto">
                            {filtered.length === 0 ? (
                                <p className="p-3 text-sm text-muted-foreground">
                                    Nenhum usuario encontrado
                                </p>
                            ) : (
                                filtered.slice(0, 10).map((u) => (
                                    <button
                                        key={u.id}
                                        type="button"
                                        className={`w-full text-left px-3 py-2 hover:bg-muted text-sm ${
                                            selectedUserId === u.id
                                                ? "bg-primary/10 font-medium"
                                                : ""
                                        }`}
                                        onClick={() => {
                                            onSelect(u.id, u.name)
                                            setSearch(u.name)
                                        }}
                                    >
                                        <span className="font-medium">{u.name}</span>
                                        <span className="text-muted-foreground ml-2">
                                            {u.email}
                                        </span>
                                    </button>
                                ))
                            )}
                        </div>
                    )}
                </Field>
            </CardContent>
        </Card>
    )
}
