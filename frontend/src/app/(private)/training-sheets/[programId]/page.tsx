"use client"

import { TrainingSheetList } from "@/components/training-sheet/training-sheet-list"
import { useParams } from "next/navigation"

const SheetsPage = () => {
    const params = useParams()

    return <TrainingSheetList programId={Number(params.programId)} />
}

export default SheetsPage
