"use client"

import { ExerciseEditForm } from "@/components/exercise/exercise-edit-form"
import { useParams } from "next/navigation"

const ExerciseEditPage = () => {
    const params = useParams()
    const isEditing = params.id !== "new"

    return <ExerciseEditForm exerciseId={isEditing ? Number(params.id) : undefined} />
}

export default ExerciseEditPage
