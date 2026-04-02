"use client"

import { TrainingExerciseEditForm } from "@/components/training-exercise/training-exercise-edit-form"
import { useParams } from "next/navigation"

const EditTrainingExercisePage = () => {
    const params = useParams()
    const isEditing = params.trainingExerciseId !== "new"

    return (
        <TrainingExerciseEditForm
            sheetId={Number(params.sheetId)}
            trainingExerciseId={isEditing ? Number(params.trainingExerciseId) : undefined}
        />
    )
}

export default EditTrainingExercisePage
