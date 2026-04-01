import { TrainingSheet } from "./training"


export type RecentStudent = {
    id: number
    name: string
    email: string
    createdAt: string
}

export type MonthlyCount = {
    month: string // "2026-01", "2026-02", etc.
    count: number
}

export type AdminDashboard = {
    totalActiveStudents: number
    totalActivePrograms: number
    newStudentsThisMonth: number
    studentsWithoutProgram: number

    // Financial
    pendingPayments: number
    monthlyRevenue: number
    activeSubscriptions: number

    // Recent students
    recentStudents: RecentStudent[]

    // Chart data
    studentsPerMonth: MonthlyCount[]
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