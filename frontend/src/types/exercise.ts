import { ExerciseCategory } from "./exercise-category"


export type Exercise = {
    id: number
    name: string
    equipment?: string
    videoUrl?: string
    description?: string
    active: boolean
  
    categoryId: number
    category?: ExerciseCategory
    createdAt?: Date
    updatedAt?: Date
  }
  