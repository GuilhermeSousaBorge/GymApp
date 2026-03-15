import { TrainingSheet } from "./training"


export type AdminDashboard = {
    totalActiveStudents: number
    totalActivePrograms: number
    newStudentsThisMonth: number
    studentsWithoutProgram: number
}


type ProgramSummary = {
     id: number
     name: string
     active: boolean
     todaySheet: TrainingSheet
     nextTrainings: TrainingSheet[]
}

export type StudentDashboard = {
    programs: ProgramSummary[]
}