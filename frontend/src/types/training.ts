import { Exercise } from "./exercise"
import { User } from "./user"

export type TrainingProgram = {
  id: number
  name: string
  description: string
  active: boolean
  student: User
  trainer?: User
  trainingSheets?: TrainingSheet[]
  trainerId?: number
  userId: number
  createdAt: string
  updatedAt: string
}

export const WEEKDAYS = {
  MONDAY: "Segunda-feira",
  TUESDAY: "Terça-feira",
  WEDNESDAY: "Quarta-feira",
  THURSDAY: "Quinta-feira",
  FRIDAY: "Sexta-feira",
  SATURDAY: "Sábado",
  SUNDAY: "Domingo"
} as const

export type Weekday = keyof typeof WEEKDAYS
export const weekDayOptions = Object.entries(WEEKDAYS).map(
  ([value, label]) => ({
    value: value as Weekday,
    label
  })
)

export type TrainingSheet = {
  id: number
  name: string
  weekdays: Weekday[]
  restTimeSeconds?: number
  orderInProgram: number
  active: boolean
  description: string

  trainingProgramId: number
  createdAt: string
  updatedAt: string
}


export type TrainingExercise = {
    id: number
    exerciseId: number
    trainingSheetId: number
  
    sets: number
    reps: string
    restTimeInSeconds?: number
    techniqueNotes?: string
    orderInSheet: number
    createdAt: string
    updatedAt: string
    exerciseInfo?: Exercise 
  }
  