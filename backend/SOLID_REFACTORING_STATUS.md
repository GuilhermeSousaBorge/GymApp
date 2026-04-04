# SOLID Refactoring - Status Real (Mar/2026)

## Resumo executivo

- Fase 1 (DIP): concluida e consolidada com ports/adapters.
- Fase 2 (SRP + UseCases): concluida no fluxo HTTP.
- Fase 3 (consolidacao ports/adapters): concluida.
- Fase 4 (OCP): concluida formalmente (base + hardening A/B/C + estabilizacao).
- Fase 5 (LSP): planejada e pronta para inicio (`SOLID_PHASE_5_PLAN.md`).
- Build: compilacao limpa valida com `clean -DskipTests compile`.
- Auditoria final de UseCases executada (metodo `execute`, `@Transactional`, `log.info`).
- Teste de contexto validado com PostgreSQL ativo em `localhost:5433`.

## O que significa "Fase 2 concluida"

Os controllers deixaram de depender de `*Service` para regras de dominio e passaram a orquestrar endpoints usando UseCases dedicados.

## Progresso por modulo (Fase 2)

### `user`
- Status: concluido.
- `UserController` usa apenas UseCases.

### `auth`
- Status: concluido.
- `AuthController` usa apenas UseCases.

### `training`
- Status: concluido.
- Controllers usam apenas UseCases, incluindo reorder via `ReorderSheetUseCase` e `ReorderExerciseUseCase`.

### `exercise`
- Status: concluido.
- `ExerciseController` e `ExerciseCategoryController` usam apenas UseCases.

### `dashboard`
- Status: concluido.
- `DashboardController` usa apenas UseCases.

## Correcoes relevantes aplicadas durante o fechamento

1. Conflito de beans entre UseCases de `training` e `exercise` com mesmo nome.
- Solucao: `@Service("...")` explicito nos pares duplicados.

2. Falha de inicializacao por query derivada invalida.
- `existeBySheetId` -> `existsByTrainingSheetId`.

3. Extracao final de fluxos ainda em service.
- Reorder de sheet -> `ReorderSheetUseCase`.
- Reorder de training exercise -> `ReorderExerciseUseCase`.

4. Ajustes de robustez para build limpo.
- Correcoes em `UserMapper` e getters explicitos nas entidades usadas pelo mapper.

5. Ajustes finais de conformidade dos UseCases (mar/2026).
- `ListExercisesFromSheetUseCase`: adicionado `@Transactional(readOnly = true)`.
- `DeleteCategoryUseCase`, `DeleteExerciseUseCase`, `DeleteProgramUseCase`, `DeleteSheetUseCase`: entrada do `execute(...)` padronizada com `log.info(...)`.

## Estado dos services antigos

- A camada `service/` legada foi removida dos modulos principais apos auditoria de referencias.
- Nao ha mais `*Service.java` em `src/main/java/backend/**/service/`.
- A orquestracao HTTP segue o fluxo atual `Controller -> UseCase -> Repository/Port`.

## Validacao executada

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q -Dtest=BackendApplicationTests test
```

Resultado mais recente:
- `compile`: OK.
- `BackendApplicationTests`: OK com PostgreSQL ativo em `localhost:5433`.

## Proximo foco recomendado

- Iniciar Fase 5 (LSP) com foco em substituibilidade de contratos (`Port`/`Adapter`) sem quebra de comportamento.
- Manter backlog de cobranca avancada como trilha evolutiva paralela (nao bloqueante do gate arquitetural).

## Progresso inicial da Fase 3 (mar/2026)

- Suite de testes unitarios criada para `user` UseCases:
  - `src/test/java/backend/user/usecase/GetUserByIdUseCaseTest.java`
  - `src/test/java/backend/user/usecase/ListUserUseCaseTest.java`
  - `src/test/java/backend/user/usecase/ActivateUserUseCaseTest.java`
  - `src/test/java/backend/user/usecase/DeactivateUserUseCaseTest.java`
  - `src/test/java/backend/user/usecase/UpdateUserUseCaseTest.java`
- Cenarios cobertos: fluxo feliz e principais falhas de regra (nao encontrado, email/cpf duplicados, role invalida).
- Execucao validada com:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q "-Dtest=GetUserByIdUseCaseTest,ListUserUseCaseTest,ActivateUserUseCaseTest,DeactivateUserUseCaseTest,UpdateUserUseCaseTest" test
```

- Suite de testes unitarios criada para `auth` UseCases:
  - `src/test/java/backend/auth/usecase/LoginUseCaseTest.java`
  - `src/test/java/backend/auth/usecase/RegisterUseCaseTest.java`
  - `src/test/java/backend/auth/usecase/MeUseCaseTest.java`
  - `src/test/java/backend/auth/usecase/ValidateTokenUseCaseTest.java`
- Suite de testes unitarios criada para `dashboard` UseCases:
  - `src/test/java/backend/dashboard/usecase/GetAdminDashboardUseCaseTest.java`
  - `src/test/java/backend/dashboard/usecase/GetStudentDashboardUseCaseTest.java`
- Limpeza guiada iniciada nos services legados sem uso em controller:
  - removido `src/main/java/backend/auth/service/AuthService.java`
  - removido `src/main/java/backend/dashboard/service/DashboardService.java`
- Validacao conjunta executada (user + auth + dashboard):

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q "-Dtest=GetUserByIdUseCaseTest,ListUserUseCaseTest,ActivateUserUseCaseTest,DeactivateUserUseCaseTest,UpdateUserUseCaseTest,LoginUseCaseTest,RegisterUseCaseTest,MeUseCaseTest,ValidateTokenUseCaseTest,GetAdminDashboardUseCaseTest,GetStudentDashboardUseCaseTest" test
```

- Suite de testes unitarios criada para `training` UseCases:
  - `src/test/java/backend/training/usecase/CreateProgramUseCaseTest.java`
  - `src/test/java/backend/training/usecase/UpdateProgramUseCaseTest.java`
  - `src/test/java/backend/training/usecase/CreateSheetUseCaseTest.java`
  - `src/test/java/backend/training/usecase/ReorderSheetUseCaseTest.java`
  - `src/test/java/backend/training/usecase/DeleteSheetUseCaseTest.java`
  - `src/test/java/backend/training/usecase/CreateExerciseUseCaseTest.java`
  - `src/test/java/backend/training/usecase/ReorderExerciseUseCaseTest.java`
  - `src/test/java/backend/training/usecase/GetSheetExercisesUseCaseTest.java`
  - `src/test/java/backend/training/usecase/DeleteProgramUseCaseTest.java`
- Suite de testes unitarios criada para `exercise` UseCases:
  - `src/test/java/backend/exercise/usecase/CreateExerciseUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/UpdateExerciseUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/DeleteExerciseUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/SearchExercisesUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/CreateCategoryUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/UpdateCategoryUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/DeleteCategoryUseCaseTest.java`
  - `src/test/java/backend/exercise/usecase/GetExerciseByCategoryUseCaseTest.java`
- Limpeza guiada concluida para services legados restantes sem referencia:
  - removido `src/main/java/backend/training/service/TrainingProgramService.java`
  - removido `src/main/java/backend/training/service/TrainingSheetService.java`
  - removido `src/main/java/backend/training/service/TrainingExerciseService.java`
  - removido `src/main/java/backend/exercise/service/ExerciseService.java`
  - removido `src/main/java/backend/exercise/service/ExerciseCategoryService.java`
  - removido `src/main/java/backend/user/service/UserService.java`
- Validacoes adicionais executadas:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q "-Dtest=CreateProgramUseCaseTest,UpdateProgramUseCaseTest,CreateSheetUseCaseTest,ReorderSheetUseCaseTest,DeleteSheetUseCaseTest,CreateExerciseUseCaseTest,ReorderExerciseUseCaseTest,GetSheetExercisesUseCaseTest,DeleteProgramUseCaseTest" test
.\mvnw.cmd -q "-Dtest=backend.exercise.usecase.CreateExerciseUseCaseTest,backend.exercise.usecase.UpdateExerciseUseCaseTest,backend.exercise.usecase.DeleteExerciseUseCaseTest,backend.exercise.usecase.SearchExercisesUseCaseTest,backend.exercise.usecase.CreateCategoryUseCaseTest,backend.exercise.usecase.UpdateCategoryUseCaseTest,backend.exercise.usecase.DeleteCategoryUseCaseTest,backend.exercise.usecase.GetExerciseByCategoryUseCaseTest" test
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q test
```

- Cobertura complementar concluida para os UseCases de `training` e `exercise`:
  - `training`: 24/24 UseCases com teste direto em `src/test/java/backend/training/usecase/`.
  - `exercise`: 14/14 UseCases com teste direto em `src/test/java/backend/exercise/usecase/`.
  - exemplos adicionados nesta etapa: `ActivateProgramUseCaseTest`, `GetProgramByIdUseCaseTest`, `UpdateSheetUseCaseTest`, `ListProgramUseCaseTest`, `GetExerciseByIdUseCaseTest`, `ListActiveCategoriesUseCaseTest`, `ListExercisesUseCaseTest`, `GetCategoryByIdUseCaseTest`.

## Auditoria arquitetural - ports, adapters e fronteiras entre modulos

### Estado consolidado dos ports/adapters

- Os pacotes `port/` e `adapter/` estao ativos em `auth`, `user`, `training`, `exercise` e `dashboard`.
- UseCases dos modulos principais passaram a depender de portas de consulta/comando/validacao.
- Auditoria de imports dos UseCases: **sem imports de `*.repository.*`** em `src/main/java/backend/**/usecase/`.
- Adapters seguem encapsulando acesso a persistence/repositories sem alterar contratos HTTP/DTO.

### Acoplamentos residuais observados

1. Dependencias cross-modulo estao intermediadas por portas (sem acesso direto a repository entre modulos).
2. Itens remanescentes sao de melhoria continua (nao bloqueiam encerramento da Fase 3).

### Prioridade sugerida para continuidade

1. Iniciar Fase 4 com plano incremental por modulo.
2. Manter auditoria arquitetural automatizada de imports/fronteiras em paralelo.

## Execucao da fase arquitetural (ports/adapters)

- Primeiro slice aplicado em `training` (baixo risco, foco em leitura):
  - `GetProgramByIdUseCase` -> `TrainingProgramQueryPort`
  - `ListProgramUseCase` -> `TrainingProgramQueryPort`
  - `GetSheetByIdUseCase` -> `TrainingSheetQueryPort`
  - `GetExerciseByIdUseCase` -> `TrainingExerciseQueryPort`
- Comportamento funcional preservado (mesmas regras, excecoes e assinatura `execute(...)`).
- Testes ajustados para mockar portas em vez de repositories:
  - `GetProgramByIdUseCaseTest`
  - `ListProgramUseCaseTest`
  - `GetSheetByIdUseCaseTest`
  - `GetExerciseByIdUseCaseTest`
- Validacao local:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q "-Dtest=backend.training.usecase.GetProgramByIdUseCaseTest,backend.training.usecase.ListProgramUseCaseTest,backend.training.usecase.GetSheetByIdUseCaseTest,backend.training.usecase.GetExerciseByIdUseCaseTest" test
```

- Segundo slice aplicado em `exercise` (foco em leitura):
  - `GetExerciseByIdUseCase` -> `ExerciseQueryPort`
  - `ListExercisesUseCase` -> `ExerciseQueryPort`
  - `ListActiveExercisesUseCase` -> `ExerciseQueryPort`
  - `SearchExercisesUseCase` -> `ExerciseQueryPort`
  - `GetExerciseByCategoryUseCase` -> `ExerciseQueryPort` + `ExerciseCategoryQueryPort`
  - `GetCategoryByIdUseCase` -> `ExerciseCategoryQueryPort`
  - `ListCategoriesUseCase` -> `ExerciseCategoryQueryPort`
  - `ListActiveCategoriesUseCase` -> `ExerciseCategoryQueryPort`
- Testes de `exercise` ajustados para mockar portas no lugar de repositories nas leituras acima.

- Terceiro slice aplicado em `training` (foco em escrita):
  - `CreateProgramUseCase`, `UpdateProgramUseCase`, `ActivateProgramUseCase`, `DeactivateProgramUseCase`, `DeleteProgramUseCase`
  - `CreateSheetUseCase`, `UpdateSheetUseCase`, `ReorderSheetUseCase`, `ActivateSheetUseCase`, `DeactivateSheetUseCase`, `DeleteSheetUseCase`
  - `CreateExerciseUseCase`, `UpdateExerciseUseCase`, `ReorderExerciseUseCase`, `DeleteExerciseUseCase`
  - migrados para `Training*QueryPort`, `Training*CommandPort`, `TrainingProgramValidationPort` e `UserQueryPort`.
- Quarto slice aplicado em `exercise` (foco em escrita):
  - `CreateExerciseUseCase`, `UpdateExerciseUseCase`, `DeleteExerciseUseCase`
  - `CreateCategoryUseCase`, `UpdateCategoryUseCase`, `DeleteCategoryUseCase`
  - migrados para `Exercise*QueryPort`, `Exercise*CommandPort`, `ExerciseValidationPort` e `ExerciseCategory*Port`.
- Acoplamento cross-modulo `exercise -> training.repository` removido em `DeleteExerciseUseCase` com nova porta `ExerciseUsagePort` e adaptador `TrainingExerciseUsageAdapter`.
- Testes de escrita de `training` e `exercise` atualizados para mockar portas.

- Quinto slice aplicado para fechamento residual em `user`, `auth`, `dashboard` e leituras de `training`:
  - evolucao de `RolePort` e `UserValidationPort` com adapters correspondentes;
  - leituras residuais de `training` consolidadas via portas de query;
  - testes ajustados para mockar portas em vez de repositories nos arquivos:
    - `src/test/java/backend/auth/usecase/LoginUseCaseTest.java`
    - `src/test/java/backend/auth/usecase/MeUseCaseTest.java`
    - `src/test/java/backend/auth/usecase/RegisterUseCaseTest.java`
    - `src/test/java/backend/dashboard/usecase/GetAdminDashboardUseCaseTest.java`
    - `src/test/java/backend/dashboard/usecase/GetStudentDashboardUseCaseTest.java`
    - `src/test/java/backend/training/usecase/GetActiveSheetsFromProgramUseCaseTest.java`
    - `src/test/java/backend/training/usecase/GetSheetFromProgramUseCaseTest.java`
    - `src/test/java/backend/training/usecase/GetSheetExercisesUseCaseTest.java`
    - `src/test/java/backend/training/usecase/ListExercisesFromSheetUseCaseTest.java`
    - `src/test/java/backend/training/usecase/GetSheetByDayOfWeekUseCaseTest.java`

Status consolidado desta etapa:
- Cobertura direta de UseCases:
  - `training`: 24/24
  - `exercise`: 14/14
- Migracao para portas concluida nos UseCases de `user`, `auth`, `dashboard`, `training` e `exercise` sem alterar contrato HTTP/DTO.

Validacao final desta rodada:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test" test
```

Resultado:
- `compile`: OK.
- Suites `user/auth/dashboard/training/exercise`: OK.

## Fase 3 - status formal

- Estado: concluida.
- Entregas concluidas:
  - migracao de UseCases para portas nos modulos principais;
  - remocao de acoplamento direto cross-modulo por repository;
  - suites unitarias alinhadas com mocks de ports;
  - consolidacao de portas cross-modulo sem contratos redundantes relevantes;
  - revisao de fronteiras DTO/mapper criticas e ampliacao de testes de borda.
- Pendencias para fechamento: nenhuma.
- Documentos oficiais desta fase:
  - `SOLID_PHASE_3_PLAN.md`
  - `SOLID_PHASE_3_SUMMARY.md`

## Fase 4 - progresso consolidado (mar/2026)

- Modelagem de negocio consolidada para assinatura e cobranca:
  - `User -> Subscription -> Plan` (sem `plan_id` direto em `users`).
  - `Payment` vinculado a `Subscription` (`subscription_id`).
- Migracoes Flyway adicionadas:
  - `src/main/resources/db/migration/V10__create_plan_tables_fix.sql`
  - `src/main/resources/db/migration/V11__create_table_subscriptions.sql`
  - `src/main/resources/db/migration/V12__create_table_payments.sql`
- Modulo `plan` implementado em padrao UseCase + Port + Adapter:
  - controller, DTOs, mapper, repository, ports e UseCases de CRUD/ativacao.
- Modulo `subscription` implementado:
  - entidade, repository, ports, adapter, mapper, controller e UseCases
    `CreateSubscriptionUseCase`, `CancelSubscriptionUseCase`, `GetActiveSubscriptionByUserUseCase`.
- Modulo `payment` implementado:
  - entidade, repository, ports, adapter, mapper, controller e UseCases
    `CreatePaymentUseCase`, `MarkPaymentAsPaidUseCase`, `ListPaymentsBySubscriptionUseCase`.
- OCP aplicado em `plan` com `PlanPolicy` + politicas concretas + `PlanPolicyResolver`.

Validacao executada nesta etapa:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

Resultado:
- `compile`: OK.
- suites `plan/subscription/payment`: OK.

- Hardening Fase 4 (Slice A) concluido:
  - ownership/authorization em leituras de `subscription` e `payment` via `Authentication` no UseCase;
  - acesso negado padronizado com `AccessDeniedException` (HTTP 403).
- Testes negativos Fase 4 (Slice B) concluidos:
  - `CreateSubscriptionUseCaseTest` com plano inativo/usuario inexistente/assinatura ativa duplicada;
  - `CreatePaymentUseCaseTest` com assinatura expirada e dados obrigatorios incompletos;
  - `MarkPaymentAsPaidUseCaseTest` com pagamento inexistente e transicao invalida.
- Regras de dominio endurecidas:
  - `CreatePaymentUseCase` valida `subscriptionId`, `amount`, `dueDate`, `paymentMethod`;
  - `MarkPaymentAsPaidUseCase` permite apenas transicao `PENDING -> PAID`.
- Observabilidade e consistencia de status Fase 4 (Slice C) concluidas:
  - logs de transicao adicionados em `CancelSubscriptionUseCase` e `MarkPaymentAsPaidUseCase`;
  - log de evento de criacao adicionado em `CreatePaymentUseCase`;
  - cancelamento de assinatura endurecido com bloqueio de `EXPIRED -> CANCELLED`;
  - criacao de pagamento endurecida para status inicial apenas `PENDING`.

- Documentos oficiais da Fase 4:
  - `SOLID_PHASE_4_PLAN.md`
  - `SOLID_PHASE_4_CONTINUITY.md`
  - `SOLID_PHASE_4_SUMMARY.md`

## Fase 4 - status formal

- Estado: concluida.
- Gate de saida atendido:
  - base OCP entregue (`plan/subscription/payment`);
  - hardening A/B/C aplicado e validado;
  - estabilizacao de testes e hotfix documentados.
- Backlog residual:
  - regras avancadas de cobranca (grace period, retry policy, estorno) seguem como evolucao funcional.

## Fase 5 - planejamento oficial

- Documento de inicio: `SOLID_PHASE_5_PLAN.md`.
- Objetivo: reforcar LSP com testes de substituibilidade de contratos e invariantes por modulo.
- Gate de inicio: documentacao sincronizada + suites de UseCase verdes.

## Ultima atualizacao

- Data: 2026-04-02
- Evidencia tecnica recente:
  - compilacao local concluida sem erros;
  - ajuste de estabilidade em testes de `dashboard` para novas dependencias de `payment/subscription`;
  - cobertura complementar adicionada para listagens por status em `payment` e `subscription`;
  - correcao de log no endpoint de listagem de pagamentos (`status`).

### Hotfix de estabilidade (abr/2026)

- `GetAdminDashboardUseCaseTest` atualizado com mocks de `PaymentQueryPort` e `SubscriptionQueryPort`, evitando `NullPointerException` apos evolucao do UseCase.
- Novos testes unitarios adicionados:
  - `src/test/java/backend/payment/usecase/ListPaymentsByStatusUseCaseTest.java`
  - `src/test/java/backend/subscription/usecase/ListByStatusUseCaseTest.java`
- Validacao executada:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q "-Dtest=backend.dashboard.usecase.GetAdminDashboardUseCaseTest,backend.payment.usecase.ListPaymentsByStatusUseCaseTest,backend.subscription.usecase.ListByStatusUseCaseTest" test
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test,backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

## Nova funcionalidade - exportacao de treino em PDF (abr/2026)

- Exportacao de treino por programa adicionada no modulo `training` com `openpdf`.
- Novo endpoint:
  - `GET /api/training-programs/{programId}/export/pdf`
  - seguranca: `@ProgramOwnerOrAdmin`
- Fluxo implementado no padrao arquitetural:
  - `TrainingProgramController` -> `ExportTrainingProgramPdfUseCase` -> `Training*QueryPort` + `TrainingProgramPdfExporterPort` -> `PdfTrainingProgramExporterAdapter`.
- Nome de arquivo padrao:
  - `programa-{programName}-{userName}.pdf` (sanitizado para slug ASCII).
- Comportamento para programa sem folhas:
  - PDF valido gerado com a mensagem: `Programa sem folhas de treino cadastradas`.
- Cobertura de testes adicionada:
  - `src/test/java/backend/training/usecase/ExportTrainingProgramPdfUseCaseTest.java`
  - `src/test/java/backend/training/adapter/PdfTrainingProgramExporterAdapterTest.java`
- Validacao executada:

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.training.usecase.ExportTrainingProgramPdfUseCaseTest,backend.training.adapter.PdfTrainingProgramExporterAdapterTest" test
.\mvnw.cmd -q "-Dtest=backend.training.usecase.*Test" test
```

