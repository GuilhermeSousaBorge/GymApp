export type Permission =
      "Administrador" | "PersonalTrainer" | "Recepcionista" | "Aluno"
  
export type Role = {
  id: number
  name: string
  description: string
  permissions: Permission[]
}
