# SOLID Phase 4 Plan - OCP: Extensibilidade (Mar/2026)

## Objetivo da fase

Aplicar o **Open-Closed Principle (OCP)**: o sistema deve ser aberto para extensao e fechado para modificacao.

Foco pratico: introduzir os modulos de **Planos**, **Assinaturas** e **Pagamentos** (`plan`, `subscription`, `payment`) usando **Strategy** em regras variaveis por tipo de plano, sem quebrar contratos existentes de UseCase, Port e Controller.

---

## Justificativa arquitetural

Fases anteriores garantiram:
- SRP: um UseCase = uma responsabilidade (Fase 2).
- DIP: UseCases dependem de Ports, não de Repositories (Fase 3).

A Fase 4 consolida o OCP introduzindo pontos de extensao explicitos via interfaces de estrategia (`PlanPolicy`), permitindo adicionar novos tipos de plano sem modificar o codigo existente.

---

## Escopo da Fase 4

### Modulo `plan` (novo)

```
plan/
  model/
    entity/Plan.java
    enums/PlanType.java
  port/
    PlanQueryPort.java
    PlanCommandPort.java
  repository/
    PlanRepository.java
  adapter/
    PlanRepositoryAdapter.java
  dto/
    PlanRequest.java
    PlanResponse.java
  mapper/
    PlanMapper.java
  usecase/
    CreatePlanUseCase.java
    UpdatePlanUseCase.java
    DeletePlanUseCase.java
    GetPlanByIdUseCase.java
    ListPlansUseCase.java
    ActivatePlanUseCase.java
    DeactivatePlanUseCase.java
  policy/
    PlanPolicy.java
    FreePlanPolicy.java
    BasicPlanPolicy.java
    PremiumPlanPolicy.java
    PlanPolicyResolver.java
  controller/
    PlanController.java
```

### Modulo `subscription` (novo)

```
subscription/
  model/entity/Subscription.java
  port/SubscriptionQueryPort.java
  port/SubscriptionCommandPort.java
  repository/SubscriptionRepository.java
  adapter/SubscriptionRepositoryAdapter.java
  dto/SubscriptionResponse.java
  mapper/SubscriptionMapper.java
  usecase/CreateSubscriptionUseCase.java
  usecase/CancelSubscriptionUseCase.java
  usecase/GetActiveSubscriptionByUserUseCase.java
```

### Modulo `payment` (novo)

```
payment/
  model/entity/Payment.java
  port/PaymentQueryPort.java
  port/PaymentCommandPort.java
  repository/PaymentRepository.java
  adapter/PaymentRepositoryAdapter.java
  dto/PaymentResponse.java
  mapper/PaymentMapper.java
  usecase/CreatePaymentUseCase.java
  usecase/MarkPaymentAsPaidUseCase.java
  usecase/ListPaymentsBySubscriptionUseCase.java
```

### Integracao com `user`

- `User` nao recebe `plan_id` direto.
- Relacao de negocio fica: `User -> Subscription -> Plan`.
- Plano atual do usuario e derivado da assinatura ativa.

### Migracoes de banco

- `V10__create_plan_tables_fix.sql`: cria/corrige `plans` e `plan_benefits` (correcao da base inicial).
- `V11__create_table_subscriptions.sql`: cria `subscriptions` com FK para `users` e `plans`.
- `V12__create_table_payments.sql`: cria `payments` com FK para `subscriptions`.

---

## Slices de entrega

### Slice 1 - Fundacao (este documento + entity + migration)
- [x] `SOLID_PHASE_4_PLAN.md`
- [x] `Plan.java` (entity com `price`, `maxStudents`, `maxPrograms`, `benefits`, `active`)
- [x] `V10__create_plan_tables_fix.sql`

### Slice 2 - Ports, Repository e Adapter
- [x] `PlanRepository.java`
- [x] `PlanQueryPort.java` + `PlanCommandPort.java`
- [x] `PlanRepositoryAdapter.java`
- [x] `SubscriptionRepository.java` + Ports + Adapter
- [x] `PaymentRepository.java` + Ports + Adapter

### Slice 3 - DTOs, Mapper e UseCases CRUD
- [x] `PlanRequest.java`, `PlanResponse.java`
- [x] `PlanMapper.java`
- [x] `CreatePlanUseCase`, `UpdatePlanUseCase`, `DeletePlanUseCase`
- [x] `GetPlanByIdUseCase`, `ListPlansUseCase`
- [x] `ActivatePlanUseCase`, `DeactivatePlanUseCase`
- [x] `CreateSubscriptionUseCase`, `CancelSubscriptionUseCase`, `GetActiveSubscriptionByUserUseCase`
- [x] `CreatePaymentUseCase`, `MarkPaymentAsPaidUseCase`, `ListPaymentsBySubscriptionUseCase`

### Slice 4 - OCP: estrategia de Policy
- [x] `PlanPolicy.java` (interface)
- [x] `FreePlanPolicy.java`, `BasicPlanPolicy.java`, `PremiumPlanPolicy.java`
- [x] `PlanPolicyResolver.java` (selecao de estrategia sem alterar UseCases)

### Slice 5 - Controller e integracao com `user`
- [x] `PlanController.java`
- [x] Endpoints para assinatura e pagamento
- [x] Exposicao de plano ativo do usuario baseada em assinatura ativa
- [x] Testes unitarios de UseCases dos modulos `plan`, `subscription` e `payment`

---

## Interface OCP central

```java
public interface PlanPolicy {
    int getMaxStudents();
    int getMaxPrograms();
    boolean allowsCustomExercises();
    boolean allowsVideoUrl();
    String getPlanType();
}
```

Cada implementacao define seus limites sem modificar codigo existente:

```java
@Component
public class FreePlanPolicy implements PlanPolicy {
    @Override public int getMaxStudents()       { return 5; }
    @Override public int getMaxPrograms()       { return 1; }
    @Override public boolean allowsCustomExercises() { return false; }
    @Override public boolean allowsVideoUrl()   { return false; }
    @Override public String getPlanType()       { return "FREE"; }
}
```

---

## Definicao de concluido da Fase 4

1. Módulo `plan` funcional com CRUD completo via UseCases + Ports.
2. `PlanPolicy` com pelo menos 3 implementacoes (Free, Basic, Premium).
3. Resolver de politica selecionando estrategia por tipo de plano sem alterar UseCases.
4. Vinculo de plano por `Subscription` (sem `plan_id` direto em `users`).
5. Testes unitarios dos UseCases dos modulos novos verdes.
6. Compilação limpa (`clean -DskipTests compile`).
7. Suites de teste de todos os módulos verdes.

## Ordem de execucao das migrations

1. `V10__create_plan_tables_fix.sql`
2. `V11__create_table_subscriptions.sql`
3. `V12__create_table_payments.sql`

Observacao: `V9__create_table_plan.sql` existe no historico, mas a correcao efetiva de schema para a fase atual fica consolidada em `V10`.

---

## Comandos de validação (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test" test
```

---

## Referências

- `SOLID_REFACTORING_STATUS.md` — histórico das fases anteriores
- `SOLID_PHASE_3_SUMMARY.md` — fechamento da Fase 3
- `SOLID_PHASE_4_CONTINUITY.md` — guia de continuidade e proximo passo da Fase 4
- `AGENTS.md` — regras obrigatórias para novos changes

---

## Ultima atualizacao

- Data: 2026-03-30
- Status: Implementacao base da Fase 4 concluida (`plan`, `subscription`, `payment`) com compilacao e testes dos novos UseCases.

