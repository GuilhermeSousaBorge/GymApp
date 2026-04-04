# SOLID Phase 3 Plan (Estado Atual)

## Objetivo da fase

Consolidar o uso de ports/adapters iniciado na Fase 1 e aplicado nos UseCases apos a Fase 2, reduzindo acoplamento entre modulos e padronizando fronteiras arquiteturais.

## Escopo da Fase 3

1. Migrar UseCases para depender de `*Port` (nao de `*Repository`).
2. Ajustar testes unitarios para mockar portas.
3. Remover acoplamentos diretos cross-modulo via repository.
4. Fechar gaps de consistencia arquitetural (DTOs/mappers cross-modulo e simplificacao de portas).

## Estado confirmado (Mar/2026)

- [x] UseCases de `user`, `auth`, `dashboard`, `training` e `exercise` migrados para portas.
- [x] Auditoria sem imports `*.repository.*` em `src/main/java/backend/**/usecase/`.
- [x] Suites de testes de UseCase principais executadas com sucesso por modulo.
- [x] Acoplamento direto `exercise -> training.repository` removido com `ExerciseUsagePort`.
- [x] Consolidacao final de portas cross-modulo (eliminar sobreposicoes e contratos redundantes).
- [x] Revisao de DTOs/mappers que ainda atravessam fronteiras de dominio.
- [x] Expansao de cenarios de teste de borda/negativo nos UseCases mais acoplados.

## O que ja foi entregue na Fase 3

### Slice 1 - `training` leitura
- `GetProgramByIdUseCase`
- `ListProgramUseCase`
- `GetSheetByIdUseCase`
- `GetExerciseByIdUseCase`

### Slice 2 - `exercise` leitura
- `GetExerciseByIdUseCase`
- `ListExercisesUseCase`
- `ListActiveExercisesUseCase`
- `SearchExercisesUseCase`
- `GetExerciseByCategoryUseCase`
- `GetCategoryByIdUseCase`
- `ListCategoriesUseCase`
- `ListActiveCategoriesUseCase`

### Slice 3 - `training` escrita
- `Create/Update/Activate/Deactivate/DeleteProgramUseCase`
- `Create/Update/Reorder/Activate/Deactivate/DeleteSheetUseCase`
- `Create/Update/Reorder/DeleteExerciseUseCase`

### Slice 4 - `exercise` escrita
- `Create/Update/DeleteExerciseUseCase`
- `Create/Update/DeleteCategoryUseCase`

### Slice 5 - fechamento residual
- Evolucao de `RolePort` e `UserValidationPort`.
- Ajustes finais de `auth`, `dashboard` e leituras residuais de `training`.
- Testes migrados para mock de ports nesses modulos.

### Slice 6 - fechamento arquitetural final
- Consolidacao de portas cross-modulo com remocao de contrato redundante (`existsByExercise`) de `TrainingExerciseQueryPort`, mantendo validacao de uso em `ExerciseUsagePort`.
- DTOs de resposta desacoplados de entidades (`TrainingProgramResponse`, `TrainingExerciseResponse`, `StudentDashboardResponse`).
- Entidades desacopladas de DTOs via interfaces de dominio (`AddressUpdatable`, `Training*Updatable`, `Exercise*Updatable`).
- Robustez em cenarios de colecao nula no dashboard e delecoes.
- Expansao de testes negativos/de borda em `training`, `exercise` e `dashboard`.

## Definicao de concluido da Fase 3

A Fase 3 sera considerada concluida quando:

1. Nao houver imports `*.repository.*` nos UseCases dos modulos principais.
2. Fluxos cross-modulo estiverem mediados por portas explicitas (sem acesso direto a repository).
3. Portas cross-modulo estiverem consolidadas sem redundancia relevante.
4. DTOs/mappers cross-modulo criticos estiverem revisados.
5. Suites de testes de UseCase por modulo estiverem verdes na validacao alvo.

## Validacao recomendada (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test" test
```

## Decisao atual

- Fase 3: **concluida**.
- Fase 4: pronta para planejamento detalhado na proxima etapa.

