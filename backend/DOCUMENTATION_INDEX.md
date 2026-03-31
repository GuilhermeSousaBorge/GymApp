# Documentacao - Indice Atual (Mar/2026)

Projeto: `backend` (GymApp)  
Status SOLID: Fase 1 concluida, Fase 2 concluida no fluxo HTTP, Fase 3 concluida, Fase 4 em execucao (base concluida, continuidade em hardening)

## Leia primeiro

1. `AGENTS.md`
- Regras atuais de arquitetura e padrao UseCase
- Convencoes para novos endpoints e regras de negocio

2. `SOLID_REFACTORING_STATUS.md`
- Estado real consolidado da Fase 2
- Evidencias de validacao e proximo foco

3. `SOLID_PHASE_3_PLAN.md`
- Escopo oficial da Fase 3 (entregas, pendencias e DoD)

4. `SOLID_PHASE_3_SUMMARY.md`
- Fechamento oficial da Fase 3 e gatilho para iniciar Fase 4

5. `QUICK_START_PHASE_2.md`
- Checklist pos-fechamento da fase
- Base para manutencao e limpeza residual

6. `SOLID_PHASE_4_CONTINUITY.md`
- Guia de continuidade da Fase 4 (slices, checklist de PR, validacao)

## Fonte de verdade (manter atualizada)

- `AGENTS.md`
- `SOLID_REFACTORING_STATUS.md`
- `SOLID_PHASE_3_PLAN.md`
- `SOLID_PHASE_3_SUMMARY.md`
- `SOLID_PHASE_4_PLAN.md`
- `SOLID_PHASE_4_CONTINUITY.md`
- `SOLID_PHASE_4_SUMMARY.md`
- `DOCUMENTATION_INDEX.md`

## Documentos historicos (nao usar como referencia principal)

- `FINAL_SUMMARY.md`
- `SESSION_SUMMARY.md`
- `SOLID_PHASE_1_SUMMARY.md`
- `README_SOLID_PHASE_1.md`
- `FILES_CREATED_PHASE_1.md`
- `USECASES_ANTES_DEPOIS.md`
- `INDICE_FINAL.md`
- `QUICK_START_PHASE_2.md`
- `SOLID_PHASE_2_PLAN.md`

Obs: esses arquivos preservam contexto de sessoes anteriores e da execucao da fase, mas o status oficial esta em `SOLID_REFACTORING_STATUS.md`.

## Estado atual resumido do codigo

- Todos os controllers principais usam apenas UseCases para regras de dominio.
- `training` recebeu UseCases especificos para reorder:
  - `ReorderSheetUseCase`
  - `ReorderExerciseUseCase`
- Conflitos de bean entre UseCases duplicados de `training` e `exercise` foram resolvidos.
- Compilacao limpa validada.
- Teste de contexto requer banco PostgreSQL ativo em `localhost:5433`.
- Fase 3 concluida com testes unitarios de UseCase por modulo.
- Testes unitarios de UseCase adicionados tambem para `auth` e `dashboard`.
- Testes unitarios de UseCase adicionados tambem para `training` e `exercise`.
- `training` e `exercise` agora possuem cobertura direta completa dos UseCases atuais.
- Camada `service/` legada removida apos auditoria de referencias sem uso.
- Migracao repository -> port concluida nos UseCases de `user`, `auth`, `dashboard`, `training` e `exercise`.
- UseCases dos modulos principais nao importam mais `*.repository.*` diretamente.
- Dependencias cross-modulo entre `training`, `exercise` e `user` estao intermediadas por portas.
- Testes de UseCase de `auth`, `dashboard` e leituras residuais de `training` foram ajustados para mockar portas.

## Comandos de validacao rapida (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test" test
```

## Proximo bloco de trabalho recomendado

- Fase 4 / extensibilidade (OCP) — **implementacao base concluida**:
  - `plan`: CRUD completo em UseCases + Ports + Adapter + Controller.
  - `subscription`: `Create`, `Cancel`, `GetActiveByUser`.
  - `payment`: `Create`, `MarkAsPaid`, `ListBySubscription`.
  - migracoes novas: `V10`, `V11`, `V12`.
- Proximo foco: seguir `SOLID_PHASE_4_CONTINUITY.md` para hardening de regras, cobertura negativa e consistencia de fronteiras.
