# SOLID Phase 4 Continuity Guide (Mar/2026)

## Objetivo

Manter a Fase 4 orientada a OCP com extensao de negocio sem quebrar contratos de `Controller`, `UseCase`, `Port` e `DTO`.

## Estado atual confirmado

- Modelagem ativa: `User -> Subscription -> Plan`.
- Pagamento vincula assinatura: `payments.subscription_id`.
- Modulos base implementados: `plan`, `subscription`, `payment`.
- OCP ativo em plano: `PlanPolicy` + `PlanPolicyResolver`.
- Migrations da fase: `V10`, `V11`, `V12`.

## Contratos que nao devem ser quebrados

- Fluxo HTTP: `Controller -> UseCase -> Port -> Adapter/Repository -> Entity -> Mapper -> DTO`.
- UseCase com metodo publico unico: `execute(...)`.
- Leitura com `@Transactional(readOnly = true)` e escrita com `@Transactional`.
- `log.info(...)` no inicio de cada `execute(...)`.
- Sem acoplamento cross-modulo por repository direto; usar portas.

## Como evoluir OCP sem regressao

1. Nova variacao de plano: criar nova implementacao de `PlanPolicy`.
2. Evitar alterar assinatura publica de UseCases existentes.
3. Reusar `PlanPolicyResolver` para selecao de regra.
4. Se surgir variacao de assinatura/pagamento, aplicar Strategy unico (1 nivel), sem cadeia de fabricas.

## Backlog recomendado da continuidade

### Slice A - hardening de regras
- [x] Reforcado ownership/authorization nas leituras:
  - `GetActiveSubscriptionByUserUseCase` valida `self` ou perfil privilegiado.
  - `ListPaymentsBySubscriptionUseCase` valida dono da assinatura ou perfil privilegiado.
- [x] Controllers ajustados para repassar `Authentication` aos UseCases de leitura endurecidos.
- [x] Resposta de acesso negado padronizada via `AccessDeniedException` (HTTP 403 no `GlobalExceptionHandler`).

### Slice B - testes negativos adicionais
- [x] `CreateSubscriptionUseCase`: plano inativo, usuario inexistente, assinatura ativa duplicada.
- [x] `CreatePaymentUseCase`: assinatura cancelada/expirada, dados obrigatorios incompletos.
- [x] `MarkPaymentAsPaidUseCase`: id inexistente e transicao invalida de status.

### Slice C - observabilidade e consistencia
- [x] Logs de evento de status adicionados nos fluxos de escrita:
  - `CancelSubscriptionUseCase`: log de transicao de `SubscriptionStatus`.
  - `CreatePaymentUseCase`: log de criacao com status inicial e assinatura.
  - `MarkPaymentAsPaidUseCase`: log de transicao de `PaymentStatus`.
- [x] Transicoes de status revisadas e endurecidas:
  - bloqueio de `EXPIRED -> CANCELLED` em assinatura;
  - criacao de pagamento apenas com status inicial `PENDING`;
  - manutencao de `PENDING -> PAID` como unica transicao valida para marcacao manual.

## Checklist para cada PR da Fase 4

- [ ] Nao introduz dependencia de `repository` dentro de `usecase`.
- [ ] Mantem DTO na resposta HTTP (sem retornar entidade).
- [ ] Mantem fronteiras `user/subscription/plan/payment` sem atalho.
- [ ] Adiciona/ajusta teste unitario de UseCase do fluxo alterado.
- [ ] Atualiza docs: `SOLID_PHASE_4_PLAN.md`, `SOLID_PHASE_4_SUMMARY.md` e este arquivo quando aplicavel.

## Comandos de validacao (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

## Ordem de leitura da documentacao da fase

1. `SOLID_PHASE_4_PLAN.md` (backlog oficial).
2. `SOLID_PHASE_4_CONTINUITY.md` (guia de execucao e proximo passo).
3. `SOLID_PHASE_4_SUMMARY.md` (snapshot do que ja foi entregue).
4. `SOLID_REFACTORING_STATUS.md` (estado global de todas as fases).

## Ultima atualizacao

- Data: 2026-04-02
- Slice A: concluido
- Slice B: concluido
- Slice C: concluido
- Estado do documento: consolidado para encerramento formal da Fase 4
- Proxima referencia de execucao: `SOLID_PHASE_5_PLAN.md`

