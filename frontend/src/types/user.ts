import { Role } from "./role"

import { Address } from "./address"

export type Gender = "MALE" | "FEMALE" | "OTHER"

export type User = {
  id: number
  name: string
  email: string
  passwordHash?: string // no front isso quase nunca vem
  cpf?: string
  phone?: string
  gender: Gender
  birthDate?: string // ISO date
  active: boolean
  roleId: number
  role?: Role
  addressId?: number
  address?: Address
  createdAt?: Date
  updatedAt?: Date
}

export type Auth = {
  user: User | null
  token: string | null
}