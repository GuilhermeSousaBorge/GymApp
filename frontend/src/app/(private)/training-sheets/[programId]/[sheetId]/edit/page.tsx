"use client"

import { TrainingSheetEditForm } from "@/components/training-sheet/training-sheet-edit-form"
import { useParams } from "next/navigation"

const EditTrainingSheetPage = () => {
    const params = useParams()
    const isEditing = params.sheetId !== "new"

    return (
        <TrainingSheetEditForm
            programId={Number(params.programId)}
            sheetId={isEditing ? Number(params.sheetId) : undefined}
        />
    )
}

export default EditTrainingSheetPage
