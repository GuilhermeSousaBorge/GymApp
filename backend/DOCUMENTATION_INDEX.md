# Documentacao - Indice Atual (Mar/2026)

Projeto: `backend` (GymApp)  
Status SOLID: Fase 1 concluida, Fase 2 concluida no fluxo HTTP, Fase 3 concluida, Fase 4 concluida (encerramento formal), Fase 5 planejada

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

7. `SOLID_PHASE_5_PLAN.md`
- Plano oficial da Fase 5 (LSP): escopo, slices, DoD e gate de Go/No-Go

8. `GUIA_ARQUITETURA_PROJETO.md`
- Guia detalhado da estrutura, arquitetura e decisoes tecnicas para estudo e replicacao

9. `GUIA_ARQUITETURA_PROJETO_V2.md`
- Versao com diagramas textuais, template SOLID copiavel e checklist de PR arquitetural

10. `FRONTEND_HANDOFF_2026-03-31.md`
- Handoff operacional para o frontend com contratos reais, roles, ownership, status e formato de erro

## Fonte de verdade (manter atualizada)

- `AGENTS.md`
- `SOLID_REFACTORING_STATUS.md`
- `SOLID_PHASE_3_PLAN.md`
- `SOLID_PHASE_3_SUMMARY.md`
- `SOLID_PHASE_4_PLAN.md`
- `SOLID_PHASE_4_CONTINUITY.md`
- `SOLID_PHASE_4_SUMMARY.md`
- `SOLID_PHASE_5_PLAN.md`
- `GUIA_ARQUITETURA_PROJETO.md`
- `GUIA_ARQUITETURA_PROJETO_V2.md`
- `FRONTEND_HANDOFF_2026-03-31.md`
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
- Fase 4 concluida formalmente com modulos `plan`, `subscription` e `payment` estabilizados.
- Hardening A/B/C consolidado e validado (ownership/autorizacao, testes negativos, observabilidade e consistencia de status).
- Exportacao de treino em PDF por programa adicionada em `training`:
  - endpoint `GET /api/training-programs/{programId}/export/pdf`;
  - arquivo `programa-{programName}-{userName}.pdf`;
  - quando nao ha folhas, PDF inclui mensagem orientativa.

## Comandos de validacao rapida (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test,backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

## Proximo bloco de trabalho recomendado

- Fase 5 / LSP — **planejamento oficial publicado** em `SOLID_PHASE_5_PLAN.md`.
- Foco imediato: validar substituibilidade de portas/adapters sem quebrar contrato de UseCase, DTO e comportamento HTTP.
- Backlog avancado de cobranca (grace period, retry policy, estorno) permanece evolutivo e nao bloqueia o inicio da Fase 5.
