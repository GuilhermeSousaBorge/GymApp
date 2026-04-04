"use client"

import { TrainingProgramEditForm } from "@/components/training-program/training-program-edit-form"
import { useParams } from "next/navigation"

const EditTrainingProgramPage = () => {
    const params = useParams()
    const isEditing = params.id !== "new"

    return <TrainingProgramEditForm programId={isEditing ? Number(params.id) : undefined} />
}

export default EditTrainingProgramPage
