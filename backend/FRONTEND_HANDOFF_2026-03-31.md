# Frontend Handoff - Estado Real do Backend (2026-03-31)

> Objetivo: alinhar o frontend com o comportamento real do backend hoje, especialmente em auth, permissões, planos, assinaturas, pagamentos e formato de erro.

---

## 1) Resumo executivo

O backend hoje está em um estado mais rígido e mais seguro do que várias anotações antigas sugerem.
O frontend deve assumir as seguintes verdades:

- Autenticação principal via **cookie HttpOnly** chamado `token`.
- O body de login/registro **não retorna o token JWT**; retorna apenas `user`.
- Todas as rotas fora de `/api/auth/**` exigem autenticação.
- `plan`, `subscription` e `payment` já têm regras reais de ownership, role e transição de status.
- O fluxo de plano do usuário agora é: **User -> Subscription -> Plan**.
- O fluxo de cobrança é: **Payment -> Subscription**.

## 1.1) Módulos completos do sistema (estado atual)

Com base na estrutura em `src/main/java/backend` e no status consolidado da refatoração (`SOLID_REFACTORING_STATUS.md` e `SOLID_PHASE_4_SUMMARY.md`), os módulos abaixo estão completos para uso no fluxo HTTP atual (controller + usecases + ports/adapters + persistência):

- `auth`: login, register, me, logout por cookie HttpOnly e validação de sessão.
- `user`: gestão de usuário, dados de perfil e base de papéis/permissões usada em autenticação/autorização.
- `dashboard`: endpoints de visão operacional (admin/aluno) com dados agregados para home/painéis.
- `training`: programas, fichas e exercícios da ficha (inclui ordenação e ativação/desativação).
- `exercise`: catálogo global de exercícios e categorias (CRUD, busca e filtros ativos).
- `plan`: catálogo de planos com ativação/desativação e política OCP (`PlanPolicy`).
- `subscription`: ciclo de assinatura do usuário (criação, cancelamento e consulta de assinatura ativa).
- `payment`: ciclo de cobrança vinculado à assinatura (criação, quitação e listagem por assinatura).

### Leitura prática para o frontend

- O backend já não depende de services legados no fluxo principal; a regra de negócio está nos UseCases.
- Para contrato de API e regras funcionais imediatas, priorize os módulos `auth`, `plan`, `subscription` e `payment` neste documento.
- Para módulos `training`, `exercise`, `user` e `dashboard`, considerar os endpoints como estáveis para integração incremental de telas.

---

## 2) Base URL e ambiente local

Ambiente `dev` confirmado:

- Backend local: `http://localhost:8080`
- Banco local: PostgreSQL em `localhost:5433`
- Cookie em `dev`: `secure=false`
- `SameSite=Lax`

Frontend local permitido em CORS:
- `http://localhost:3000`
- `http://localhost:3001`
- `https://gym-app-chi-rose.vercel.app/`

### Importante para o frontend
Se usar `fetch`, enviar sempre credenciais:

```ts
fetch("http://localhost:8080/api/auth/me", {
  credentials: "include",
});
```

Se usar Axios:

```ts
axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
});
```

---

## 3) Autenticação e sessão

## Endpoints públicos
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/logout`
- `GET /api/auth/me` exige autenticação

## Regra real de login/registro
### Request de login
`POST /api/auth/login`

```json
{
  "email": "admin@academia.com",
  "password": "admin123"
}
```

### Response real de login
```json
{
  "user": {
    "id": 1,
    "name": "Joao",
    "email": "admin@academia.com",
    "active": true,
    "role": {
      "id": 1,
      "name": "Administrador",
      "description": "...",
      "permissions": []
    }
  }
}
```

### Importante
- O JWT é enviado em **cookie HttpOnly**, não no body.
- Não espere `{ user, token }` na resposta HTTP final.
- `LoginResponse` interno ainda tem `token`, mas `AuthResponse` exposto pelo controller tem **apenas `user`**.

## Logout
`POST /api/auth/logout`
- limpa o cookie `token`
- retorna `204 No Content`

## Me
`GET /api/auth/me`
- retorna `UserResponse`
- usar para reidratar sessão ao abrir app

### Recomendação para o front
- Não armazenar token em `localStorage` como fonte primária.
- Usar `me` para validar sessão atual.
- Tratar `401` como sessão inválida/expirada.

---

## 4) Registro (`register`) - contrato atual

### Request real hoje
`POST /api/auth/register`

```json
{
  "name": "Maria",
  "email": "maria@email.com",
  "password": "123456",
  "gender": "FEMALE"
}
```

### Observação importante
O DTO atual de registro **não exige** mais estes campos no backend hoje:
- `cpf`
- `phone`
- `birthDate`

Se o frontend ainda estiver exigindo isso obrigatoriamente no fluxo de cadastro inicial, está mais restritivo que o backend atual.

---

## 5) Papéis e permissões práticas

Papéis relevantes confirmados no backend:
- `Administrador`
- `PersonalTrainer`

### Regras práticas de acesso
- Admin: acesso total aos endpoints administrativos.
- PersonalTrainer: pode criar/cancelar assinatura e criar/quitar pagamento.
- Usuário autenticado comum: pode consultar recursos permitidos a ele, sujeito a ownership.

### Ownership já aplicado no backend
Mesmo autenticado, o usuário comum **não pode** consultar qualquer assinatura/pagamento arbitrário.

---

## 6) Planos (`/api/plans`)

## Visão geral
Todos os endpoints de `plans` exigem autenticação.
Mutações são só para admin.

## Endpoints
### `GET /api/plans?activeOnly=false`
- qualquer autenticado
- se `activeOnly=true`, backend retorna só planos ativos

### `GET /api/plans/{id}`
- qualquer autenticado

### `POST /api/plans`
- apenas `Administrador`

Request:
```json
{
  "name": "Premium",
  "description": "Plano premium",
  "price": 99.90,
  "maxStudents": 100,
  "maxPrograms": 10,
  "benefits": ["VIDEO", "CUSTOM_EXERCISES"]
}
```

### `PUT /api/plans/{id}`
- apenas `Administrador`
- aceita atualização parcial por campos opcionais no `PlanUpdateRequest`

### `PATCH /api/plans/{id}/activate`
- apenas `Administrador`
- retorna `204`

### `PATCH /api/plans/{id}/deactivate`
- apenas `Administrador`
- retorna `204`

### `DELETE /api/plans/{id}`
- apenas `Administrador`
- retorna `204`

## Shape de resposta (`PlanResponse`)
```json
{
  "id": 1,
  "name": "Premium",
  "description": "...",
  "price": 99.90,
  "maxStudents": 100,
  "maxPrograms": 10,
  "benefits": ["VIDEO"],
  "active": true,
  "createdAt": "2026-03-31T10:00:00",
  "updatedAt": "2026-03-31T10:00:00"
}
```

## Regra de UI
- Se o usuário não for admin, o frontend deve ocultar criar/editar/ativar/desativar/excluir.
- Para telas públicas/autenticadas, usar preferencialmente `activeOnly=true` quando a intenção for mostrar planos contratáveis.

---

## 7) Assinaturas (`/api/subscriptions`)

## Fluxo de negócio atual
- Usuário não recebe plano direto.
- O plano atual é derivado da assinatura ativa.

## Endpoints
### `POST /api/subscriptions`
- apenas `Administrador` ou `PersonalTrainer`
- cria assinatura

Request:
```json
{
  "planId": 1,
  "userId": 10,
  "startDate": "2026-03-31T10:00:00",
  "endDate": null,
  "autoRenew": true
}
```

### Regras reais do backend
- não cria se o usuário já tiver assinatura ativa
- não cria se o plano estiver inativo
- não cria se o usuário não existir
- `planPriceAtStart` é congelado com o preço do plano no momento da criação
- status inicial: `ACTIVE`

### `PATCH /api/subscriptions/{id}/cancel`
- apenas `Administrador` ou `PersonalTrainer`
- retorna `204`

### Regras reais do cancelamento
- não cancela se já estiver `CANCELLED`
- não cancela se estiver `EXPIRED`
- ao cancelar:
  - status vira `CANCELLED`
  - `cancelledAt` é preenchido
  - `autoRenew=false`
  - `endDate` recebe `now()` se ainda estiver nulo

### `GET /api/subscriptions/users/{userId}/active`
- autenticado
- **ownership ativo**:
  - o próprio usuário pode consultar a sua
  - `Administrador` e `PersonalTrainer` também podem
  - outro usuário comum recebe `403`

### `GET /api/subscriptions/me/active`
- autenticado
- melhor endpoint para área logada do aluno

## Status possíveis de assinatura
- `ACTIVE`
- `PAST_DUE`
- `CANCELLED`
- `EXPIRED`

## Shape de resposta (`SubscriptionResponse`)
```json
{
  "id": 1,
  "planId": 2,
  "planName": "Premium",
  "userId": 10,
  "userName": "Maria",
  "startDate": "2026-03-31T10:00:00",
  "endDate": null,
  "status": "ACTIVE",
  "cancelledAt": null,
  "autoRenew": true,
  "planPriceAtStart": 99.90,
  "createdAt": "2026-03-31T10:00:00",
  "updatedAt": "2026-03-31T10:00:00"
}
```

## Regras de UI recomendadas
- Tela “Meu plano” deve consumir `GET /api/subscriptions/me/active`.
- Não derive plano atual do usuário por outro caminho.
- Botão de cancelamento só deve aparecer para perfis autorizados no painel administrativo/trainer.
- `CANCELLED` e `EXPIRED` devem ser tratados como estados não ativos.

---

## 8) Pagamentos (`/api/payments`)

## Endpoints
### `POST /api/payments`
- apenas `Administrador` ou `PersonalTrainer`
- cria pagamento

Request:
```json
{
  "subscriptionId": 1,
  "amount": 99.90,
  "dueDate": "2026-04-10",
  "paymentMethod": "PIX",
  "status": "PENDING"
}
```

### Regras reais de criação
- `subscriptionId` obrigatório
- `amount` obrigatório e `> 0`
- `dueDate` obrigatório
- `paymentMethod` obrigatório
- assinatura deve existir
- assinatura **não pode** estar `CANCELLED` ou `EXPIRED`
- pagamento **só pode nascer com status `PENDING`**

### `PATCH /api/payments/{id}/pay`
- apenas `Administrador` ou `PersonalTrainer`
- retorna `204`

### Regras reais da quitação manual
- pagamento deve existir
- se já estiver `PAID`, erro
- só aceita transição `PENDING -> PAID`
- estados como `FAILED`, `CANCELLED` e `REFUNDED` não podem virar `PAID` por esse endpoint

### `GET /api/payments/subscriptions/{subscriptionId}`
- autenticado
- **ownership ativo**:
  - dono da assinatura pode listar
  - `Administrador` e `PersonalTrainer` podem listar
  - outro usuário comum recebe `403`

## Status possíveis de pagamento
- `PENDING`
- `PAID`
- `FAILED`
- `CANCELLED`
- `REFUNDED`

## Métodos possíveis
- `PIX`
- `CREDIT_CARD`
- `BOLETO`
- `CASH`

## Shape de resposta (`PaymentResponse`)
```json
{
  "id": 1,
  "subscriptionId": 1,
  "status": "PENDING",
  "amount": 99.90,
  "dueDate": "2026-04-10",
  "paymentDate": null,
  "paymentMethod": "PIX",
  "createdAt": "2026-03-31T10:00:00",
  "updatedAt": "2026-03-31T10:00:00"
}
```

## Regras de UI recomendadas
- Botão “Marcar como pago” só deve existir para admin/trainer.
- Só mostrar esse botão se `status === "PENDING"`.
- Usuário comum só pode visualizar pagamentos da própria assinatura.

---

## 8.1) Usuários (`/api/users`)

## Endpoints
### `GET /api/users`
- autenticado
- backend aplica regras por perfil no UseCase de listagem

### `GET /api/users/{id}`
- `Administrador`, `PersonalTrainer` ou o próprio usuário (`id == principal`)

### `PUT /api/users/{id}`
- `Administrador`, `PersonalTrainer` ou o próprio usuário

### `PUT /api/users/{id}/deactivate`
- apenas `Administrador`
- retorna `204`

### `PUT /api/users/{id}/activate`
- apenas `Administrador`
- retorna `204`

## Regras de UI recomendadas
- Tela de perfil pode editar apenas o próprio usuário para contas comuns.
- Ações de ativar/desativar devem aparecer apenas para admin.
- Para listagem administrativa, tratar possibilidade de filtro/restrição por perfil no backend.

---

## 8.2) Dashboard (`/api/dashboard`)

## Endpoints
### `GET /api/dashboard/admin`
- apenas `Administrador`
- retorna `AdminDashboardResponse`

### `GET /api/dashboard/student`
- autenticado
- retorna `StudentDashboardResponse` usando o usuário da sessão

## Regras de UI recomendadas
- Renderizar dashboard por perfil (admin vs aluno).
- Não tentar montar painel admin para perfis sem role `Administrador`.

---

## 8.3) Exercícios e Categorias

## Exercícios (`/api/exercises`)
### `GET /api/exercises`
- autenticado
- filtros suportados:
  - `activeOnly=true`
  - `categoryId={id}`
  - `search={termo}`

### `GET /api/exercises/{id}`
- autenticado

### `POST /api/exercises`
- `Administrador` ou `PersonalTrainer`
- retorna `201`

### `PUT /api/exercises/{id}`
- `Administrador` ou `PersonalTrainer`

### `DELETE /api/exercises/{id}`
- apenas `Administrador`
- retorna `204`

## Categorias (`/api/exercise-categories`)
### `GET /api/exercise-categories`
- autenticado
- aceita `activeOnly=true`

### `GET /api/exercise-categories/{id}`
- autenticado

### `POST /api/exercise-categories`
- `Administrador` ou `PersonalTrainer`
- retorna `201`

### `PUT /api/exercise-categories/{id}`
- `Administrador` ou `PersonalTrainer`

### `DELETE /api/exercise-categories/{id}`
- apenas `Administrador`
- retorna `204`

## Regras de UI recomendadas
- Catálogo público autenticado: priorizar `activeOnly=true` em listagens de seleção.
- Operações de CRUD devem ser exibidas só para admin/trainer (delete só admin).
- Campo de busca pode usar `search` no mesmo endpoint de listagem.

---

## 8.4) Treinos (Programas, Fichas e Exercícios da Ficha)

## Programas (`/api/training-programs`)
### `GET /api/training-programs?userId={id}`
- `Administrador`, `PersonalTrainer` ou o próprio usuário (`userId == principal`)

### `GET /api/training-programs/{programId}`
- dono do programa ou admin (`@ProgramOwnerOrAdmin`)

### `POST /api/training-programs`
- autenticado (estado atual do controller)
- retorna `201`

### `PUT /api/training-programs/{programId}`
- dono do programa ou admin

### `PATCH /api/training-programs/{programId}/activate`
- dono do programa ou admin
- retorna `204`

### `PATCH /api/training-programs/{programId}/deactivate`
- dono do programa ou admin
- retorna `204`

### `DELETE /api/training-programs/{programId}`
- dono do programa ou admin
- retorna `204`

## Fichas (`/api/training-sheets`)
### `GET /api/training-sheets?programId={id}&activeOnly={bool}`
- dono do programa ou admin
- `programId` é obrigatório no estado atual

### `GET /api/training-sheets/{sheetId}`
- dono da ficha ou admin (`@SheetOwnerOrAdmin`)

### `POST /api/training-sheets`
- autenticado (estado atual do controller)
- retorna `201`

### `PUT /api/training-sheets/{sheetId}`
- dono da ficha ou admin

### `PATCH /api/training-sheets/{sheetId}/reorder?newOrder={n}`
- dono da ficha ou admin

### `PATCH /api/training-sheets/{sheetId}/activate`
- dono da ficha ou admin
- retorna `204`

### `PATCH /api/training-sheets/{sheetId}/deactivate`
- dono da ficha ou admin
- retorna `204`

### `DELETE /api/training-sheets/{sheetId}`
- dono da ficha ou admin
- retorna `204`

## Exercícios da Ficha (`/api/training-exercises`)
### `GET /api/training-exercises?sheetId={id}`
- dono da ficha ou admin

### `GET /api/training-exercises/{exerciseId}`
- dono do exercício na ficha ou admin

### `POST /api/training-exercises`
- autenticado (estado atual do controller)
- retorna `201`

### `PUT /api/training-exercises/{exerciseId}`
- dono do exercício na ficha ou admin

### `PATCH /api/training-exercises/{exerciseId}/reorder?newOrder={n}`
- dono do exercício na ficha ou admin

### `DELETE /api/training-exercises/{exerciseId}`
- dono do exercício na ficha ou admin
- retorna `204`

## Regras de UI recomendadas
- Fluxo sugerido: Programa -> Fichas -> Exercícios da Ficha (encadeamento por IDs).
- A tela de aluno deve carregar dados do próprio usuário para evitar `403` por ownership.
- Recursos de reorder devem enviar `newOrder` por query string.

---

## 9) Formato de erro padrao

## Erro de validação (`@Valid`) - HTTP 400
```json
{
  "timestamp": "2026-03-31T10:00:00",
  "status": 400,
  "errors": {
    "email": "Email é obrigatório",
    "password": "Senha deve ter no mínimo 6 caracteres"
  }
}
```

## Erro de regra de negócio - HTTP 400
```json
{
  "timestamp": "2026-03-31T10:00:00",
  "status": 400,
  "message": "Plano inativo nao pode receber novas assinaturas"
}
```

## Erro de autenticação - HTTP 401
```json
{
  "timestamp": "2026-03-31T10:00:00",
  "status": 401,
  "message": "Email ou senha inválidos"
}
```

## Erro de autorização / ownership - HTTP 403
```json
{
  "timestamp": "2026-03-31T10:00:00",
  "status": 403,
  "message": "Sem permissao para consultar pagamentos desta assinatura"
}
```

## Erro inesperado - HTTP 500
```json
{
  "timestamp": "2026-03-31T10:00:00",
  "status": 500,
  "message": "Erro interno do servidor"
}
```

---

## 10) Divergencias importantes que o frontend deve considerar

### 1. Login/registro nao devolvem token no body final
Mesmo que existam comentarios antigos sugerindo `{ user, token }`, o response real do controller hoje devolve apenas:

```json
{
  "user": {
    "id": 1,
    "name": "Joao",
    "email": "admin@academia.com"
  }
}
```

O token vai no cookie HttpOnly.

### 2. Registro atual esta mais simples
Hoje `RegisterRequest` aceita apenas:
- `name`
- `email`
- `password`
- `gender`

### 3. Ownership ja esta valendo em subscription/payment
O frontend nao deve assumir que qualquer usuário logado pode consultar qualquer recurso por ID.

### 4. Fluxo de plano do usuário mudou
O frontend nao deve mais tratar “plano atual do usuário” como atributo direto do usuário.

### 5. CORS atual merece atenção
O backend usa endpoints `PATCH`, mas `CorsConfig` hoje lista apenas:
- `GET`
- `POST`
- `PUT`
- `DELETE`
- `OPTIONS`

Isso pode impactar browser clients cross-origin em chamadas `PATCH`.

### Recomendação
- Se o front estiver em browser e consumir `PATCH` cross-origin, validar imediatamente esse comportamento em ambiente real.
- Se houver bloqueio de preflight, o backend precisará liberar `PATCH` no CORS.

---

## 11) Checklist de adaptação do frontend

## Auth
- [ ] usar `withCredentials` / `credentials: include`
- [ ] parar de depender de token no body do login
- [ ] usar `GET /api/auth/me` para restaurar sessão
- [ ] tratar `401` como logout forçado/sessão inválida

## Role/visibilidade
- [ ] esconder ações admin para quem não tiver role `Administrador`
- [ ] esconder ações operacionais de cobrança/assinatura para quem não for `Administrador` ou `PersonalTrainer`

## Planos
- [ ] usar `GET /api/plans?activeOnly=true` para catálogo contratável
- [ ] não expor botões de edição para usuário comum

## Assinaturas
- [ ] usar `GET /api/subscriptions/me/active` na área logada do aluno
- [ ] não assumir `planId` em `user`
- [ ] mapear status `ACTIVE`, `PAST_DUE`, `CANCELLED`, `EXPIRED`

## Pagamentos
- [ ] exibir ação de quitação só quando `status === "PENDING"`
- [ ] não permitir frontend criar pagamento com status inicial diferente de `PENDING`
- [ ] mapear estados `PENDING`, `PAID`, `FAILED`, `CANCELLED`, `REFUNDED`

## Erros
- [ ] tratar payload com `errors` para validação de formulário
- [ ] tratar payload com `message` para regras de negócio/autorização

---

## 12) Endpoints mais importantes para o front hoje

### Sessão
- `POST /api/auth/login`
- `POST /api/auth/register`
- `GET /api/auth/me`
- `POST /api/auth/logout`

### Planos
- `GET /api/plans?activeOnly=true`
- `GET /api/plans/{id}`

### Assinatura do usuário
- `GET /api/subscriptions/me/active`

### Pagamentos do usuário
- `GET /api/payments/subscriptions/{subscriptionId}`

### Usuário logado
- `GET /api/users/{id}`
- `PUT /api/users/{id}`

### Treino do usuário
- `GET /api/training-programs?userId={id}`
- `GET /api/training-sheets?programId={id}&activeOnly=true`
- `GET /api/training-exercises?sheetId={id}`

### Catálogo de exercícios
- `GET /api/exercise-categories?activeOnly=true`
- `GET /api/exercises?activeOnly=true`

---

## 13) Matriz única (frontend-ready)

> Índice rápido para integração. As regras completas e exceções continuam detalhadas nas seções anteriores.

| Módulo | Endpoint | Role/Acesso | Uso na UI |
|---|---|---|---|
| `auth` | `POST /api/auth/login` | público | Login e criação de sessão por cookie HttpOnly |
| `auth` | `GET /api/auth/me` | autenticado | Reidratar sessão ao iniciar app |
| `auth` | `POST /api/auth/logout` | público | Encerrar sessão e limpar cookie |
| `user` | `GET /api/users/{id}` | admin/trainer/próprio usuário | Perfil do usuário logado ou visão administrativa |
| `user` | `PUT /api/users/{id}` | admin/trainer/próprio usuário | Edição de perfil |
| `dashboard` | `GET /api/dashboard/admin` | admin | Painel administrativo |
| `dashboard` | `GET /api/dashboard/student` | autenticado | Home do aluno com dados resumidos |
| `plan` | `GET /api/plans?activeOnly=true` | autenticado | Catálogo de planos contratáveis |
| `plan` | `POST /api/plans` | admin | Gestão administrativa de planos |
| `subscription` | `GET /api/subscriptions/me/active` | autenticado | Exibir plano atual do usuário |
| `subscription` | `POST /api/subscriptions` | admin/trainer | Criar assinatura para aluno |
| `payment` | `GET /api/payments/subscriptions/{subscriptionId}` | owner/admin/trainer | Histórico de cobrança da assinatura |
| `payment` | `PATCH /api/payments/{id}/pay` | admin/trainer | Ação manual de quitação |
| `exercise` | `GET /api/exercise-categories?activeOnly=true` | autenticado | Popular filtros de categorias |
| `exercise` | `GET /api/exercises?activeOnly=true&categoryId={id}&search={termo}` | autenticado | Catálogo e busca de exercícios |
| `exercise` | `POST /api/exercises` | admin/trainer | Cadastro de exercício global |
| `training` | `GET /api/training-programs?userId={id}` | admin/trainer/próprio usuário | Listar programas do aluno |
| `training` | `GET /api/training-sheets?programId={id}&activeOnly=true` | owner/admin | Listar fichas ativas do programa |
| `training` | `GET /api/training-exercises?sheetId={id}` | owner/admin | Listar exercícios da ficha |
| `training` | `PATCH /api/training-sheets/{sheetId}/reorder?newOrder={n}` | owner/admin | Reordenar fichas na UI de edição |

---

## 14) Conclusao

O frontend deve se adaptar principalmente a quatro pontos:
1. auth por cookie HttpOnly;
2. plano atual derivado de assinatura ativa;
3. ownership real em `subscription` e `payment`;
4. transições de status mais rígidas para cobrança.

Se o frontend alinhar esses pontos, ele passa a refletir corretamente o estado real do backend em 2026-03-31.
