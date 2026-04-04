"use client"

import { PlanEditForm } from "@/components/plan/plan-edit-form"
import { useParams } from "next/navigation"

const PlanEditPage = () => {
    const params = useParams()
    const isEditing = params.id !== "new"

    return <PlanEditForm planId={isEditing ? Number(params.id) : undefined} />
}

export default PlanEditPage
