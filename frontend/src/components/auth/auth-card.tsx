import React from "react"
import { Card } from "../ui/card"


export const AuthCard = ({children} : {children: React.ReactNode}) => {

    return(
        <Card className="w-full max-w-md shadow-xl border-none p-4">
            {children}
        </Card>
    )
}