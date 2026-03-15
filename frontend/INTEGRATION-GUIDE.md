# Guia de Integração - Mocks com suas Stores

Este guia mostra como integrar os mocks com suas stores **já existentes**.

## 📋 Passo a Passo

### 1. Copie a pasta `mocks` para seu projeto

```bash
cp -r mocks src/
```

### 2. Seus arquivos já estão prontos! 🎉

Seus formulários de **login** e **register** já estão integrados corretamente:

#### ✅ Login Form
```typescript
const onSubmit = async (data: LoginFormData) => {
  try {
    setLoading(true)
    const response = await authService.login(data) // ← Já usa o formato correto
    login(response) // ← Sua store já espera { user, token }
    router.push("/dashboard")
  } catch(err) {
    toast.error("Email ou senha invalidos", {position: "top-right"})
  } finally {
    setLoading(false)
  }
}
```

#### ✅ Register Form (atualizado)
Veja o arquivo `register-form-integrated.tsx` para a versão atualizada que:
- Chama `authService.register(data)`
- Faz login automático após registro
- Mostra toast de sucesso/erro
- Redireciona para o dashboard

### 3. Ajustes Necessários

#### A. Importe o authService nos seus formulários

**No `login-form.tsx`:**
```typescript
// Troque esta linha:
import { authService } from "@/services/auth";

// Por esta:
import { authService } from "@/mocks";
```

**No `register-form.tsx`:**
```typescript
// Adicione no topo:
import { authService } from "@/mocks";
import { useAuth } from "@/stores/auth";
import { useUi } from "@/stores/ui";
import { useRouter } from "next/navigation";
```

#### B. Hydrate da Store Auth

No seu `layout.tsx` ou `_app.tsx`, adicione o hydrate:

```typescript
'use client'

import { useAuth } from '@/stores/auth'
import { useEffect } from 'react'

export default function RootLayout({ children }) {
  const { hydrate } = useAuth()
  
  useEffect(() => {
    hydrate() // Restaura sessão do localStorage
  }, [hydrate])

  return (
    <html>
      <body>{children}</body>
    </html>
  )
}
```

### 4. Credenciais de Teste

Use estas credenciais para testar o login:

```
Email: admin@academia.com
Senha: admin123
```

Outros usuários disponíveis:
- **Personal**: personal@academia.com / admin123
- **Recepcionista**: recepcao@academia.com / admin123
- **Alunos**: ana.costa@email.com, pedro.henrique@email.com, etc / admin123

### 5. Usando outros serviços

#### User Service

```typescript
import { userService } from '@/mocks'

// Buscar todos os usuários
const users = await userService.getAll()

// Buscar com filtros
const activeUsers = await userService.getAll({ active: true })
const admins = await userService.getAll({ roleId: 1 })

// Buscar por ID
const user = await userService.getById(1)

// Criar usuário
const newUser = await userService.create({
  name: "João Silva",
  email: "joao@email.com",
  password: "senha123",
  gender: "MALE",
  roleId: 4
})

// Atualizar usuário
await userService.update(1, { name: "João Silva Atualizado" })

// Buscar alunos
const students = await userService.getStudents()
```

#### Training Service

```typescript
import { trainingService } from '@/mocks'

// Buscar programas de um aluno
const programs = await trainingService.getProgramsByUser(4)

// Buscar fichas de um programa
const sheets = await trainingService.getSheetsByProgram(1)

// Buscar exercícios de uma ficha
const exercises = await trainingService.getExercisesBySheet(1)

// Criar programa
const program = await trainingService.createProgram({
  name: "Programa de Hipertrofia",
  description: "Foco em ganho de massa",
  objective: "HYPERTROPHY",
  userId: 4,
  createdByUserId: 2
})

// Criar ficha
const sheet = await trainingService.createSheet({
  name: "Treino A - Peito e Tríceps",
  trainingType: "A",
  weekFrequency: "2x por semana",
  weekDays: [1, 4],
  restTimeDefault: 60,
  orderInProgram: 1,
  trainingProgramId: program.id
})
```

#### Payment Service

```typescript
import { paymentService } from '@/mocks'

// Buscar pagamentos de um usuário
const payments = await paymentService.getByUser(4)

// Buscar pendentes
const pending = await paymentService.getPending()

// Buscar atrasados
const overdue = await paymentService.getOverdue()

// Estatísticas
const stats = await paymentService.getStats({
  month: 1,
  year: 2026
})

// Registrar pagamento
await paymentService.registerPayment(3, {
  paymentDate: "2026-01-27",
  paymentMethod: "PIX"
})
```

### 6. Integração com useTraining Store

Sua store de training já está preparada! Exemplo de uso:

```typescript
import { useTraining } from '@/stores/training'
import { trainingService } from '@/mocks'

function TrainingPage() {
  const { loadProgram, selectSheet, activeProgram, sheets, selectedSheet } = useTraining()

  useEffect(() => {
    async function loadData() {
      // Busca programa do usuário
      const programs = await trainingService.getProgramsByUser(4)
      const program = programs[0]
      
      // Busca fichas do programa
      const programSheets = await trainingService.getSheetsByProgram(program.id)
      
      // Carrega na store
      loadProgram(program, programSheets)
    }
    
    loadData()
  }, [])

  return (
    <div>
      <h1>{activeProgram?.name}</h1>
      {sheets.map(sheet => (
        <button key={sheet.id} onClick={() => selectSheet(sheet.id)}>
          {sheet.name}
        </button>
      ))}
      
      {selectedSheet && (
        <div>
          <h2>{selectedSheet.name}</h2>
          {/* Renderizar exercícios da ficha */}
        </div>
      )}
    </div>
  )
}
```

## 🔧 Dicas Importantes

1. **Delay simulado**: Todos os serviços têm delay (300-800ms) para simular latência
2. **Dados voláteis**: Os dados resetam ao recarregar (exceto auth que usa localStorage)
3. **Validações**: Os mocks já validam emails duplicados, campos obrigatórios, etc
4. **Toast feedback**: Use `toast` do Sonner para feedback ao usuário

## 🎯 Próximos Passos

1. ✅ Integre o authService nos formulários
2. ✅ Adicione hydrate no layout
3. ✅ Teste login/register
4. ✅ Implemente telas que usam os outros serviços
5. 🔄 Quando backend estiver pronto, troque `@/mocks` por `@/services/api`

## ⚠️ Lembre-se

Os mocks são **temporários**. Quando o backend estiver pronto:
- Mantenha a mesma **interface** dos métodos
- Apenas troque o **import** de `@/mocks` para `@/services/api`
- Tudo continuará funcionando sem alterações no código dos componentes!