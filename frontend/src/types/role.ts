export type Permission =
  // | "users.read"
  // | "users.write"
  // | "training.manage"
  // | "payments.read"
  // | "payments.manage"
  // | "admin"
      "Administrador" | "PersonalTrainer" | "Recepcionista" | "Aluno"
  
export type Role = {
  id: number
  name: string
  description: string
  permissions: Permission[]
}
