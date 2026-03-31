# GymApp Backend - AGENTS Guide

## Quick context
- Stack: Java 17, Spring Boot 4.0.2, Maven Wrapper (`mvnw.cmd`).
- Default profile: `dev` (`src/main/resources/application.yaml`).
- Local DB: PostgreSQL at `localhost:5433` (`docker-compose.yml`).
- Active architecture baseline: Fase 2 + 3 concluida; Fase 4 em andamento (`SOLID_REFACTORING_STATUS.md`).

## Golden flow (do not bypass)
- HTTP path is `Controller -> UseCase -> Port -> Adapter/Repository -> Entity -> Mapper -> DTO`.
- Controllers only orchestrate HTTP/security; business rules stay in UseCases.
- UseCases use ports, not repositories directly (see `src/main/java/backend/*/usecase`).
- API responses are DTOs, not entities (see `PlanController`, `PaymentController`).

## Mandatory UseCase rules
- One public method only: `execute(...)`.
- Reads: `@Transactional(readOnly = true)`; writes: `@Transactional`.
- Start `execute(...)` with `log.info(...)`.
- Keep responsibility narrow (one action per UseCase).
- Follow naming `{Verb}{Noun}UseCase` (Get/List/Create/Update/Delete/Activate/Deactivate/Reorder).

## Cross-module boundaries already enforced
- User plan relation is indirect: `User -> Subscription -> Plan`.
- Payment links to subscription (`payments.subscription_id`), not user directly.
- Avoid cross-module repository coupling; expose a port instead.
- Example: `exercise` checks usage via `ExerciseUsagePort`; implemented in `training` by `TrainingExerciseUsageAdapter`.

## Known pitfalls from this codebase
- Duplicate UseCase names across modules require explicit bean names.
- Example in code: `@Service("trainingCreateExerciseUseCase")` vs `@Service("exerciseCreateExerciseUseCase")`.
- Spring Data derived queries must match field names exactly.
- Example: `existsByTrainingSheetId(Long sheetId)`.

## OCP pattern in phase 4
- Plan variability uses one Strategy level: `PlanPolicy` + `PlanPolicyResolver`.
- Add new plan behavior by creating a new `PlanPolicy` implementation; avoid changing existing UseCase contracts.
- Reference: `src/main/java/backend/plan/policy/PlanPolicy.java` and `PlanPolicyResolver.java`.

## Dev workflows (PowerShell)
```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
docker compose up -d
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test,backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

## Key files to read before changing code
- `SOLID_REFACTORING_STATUS.md`
- `SOLID_PHASE_4_PLAN.md`
- `src/main/java/backend/plan/controller/PlanController.java`
- `src/main/java/backend/subscription/usecase/CreateSubscriptionUseCase.java`
- `src/main/java/backend/payment/usecase/CreatePaymentUseCase.java`
- `src/main/resources/db/migration/V11__create_table_subscriptions.sql`
- `src/main/resources/db/migration/V12__create_table_payments.sql`
