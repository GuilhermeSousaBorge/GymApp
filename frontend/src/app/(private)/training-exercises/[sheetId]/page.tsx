"use client"

import { TrainingExerciseList } from "@/components/training-exercise/training-exercise-list"
import { useParams } from "next/navigation"

const TrainingExercisesPage = () => {
    const params = useParams()

    return <TrainingExerciseList sheetId={Number(params.sheetId)} />
}

export default TrainingExercisesPage
