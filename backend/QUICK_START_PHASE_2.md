# Quick Start - Pos Fase 2 (SRP + UseCases)

Este guia foi atualizado para o estado atual do projeto: fluxo HTTP migrado para `Controller -> UseCase`.

## Estado consolidado

- [x] Controllers principais sem injecao direta de `*Service` para regra de dominio.
- [x] UseCases com metodo publico unico `execute(...)`.
- [x] Padrao transacional aplicado (`@Transactional` write e `@Transactional(readOnly = true)` read).
- [x] `log.info(...)` na entrada do `execute(...)` em todos os UseCases auditados.
- [x] Casos de duplicidade de nome entre modulos resolvidos com `@Service("...")` explicito.

## O que validar antes de seguir para a Fase 3

1. Banco PostgreSQL local ativo em `localhost:5433` (subido via `docker-compose.yml`).
2. Build limpo sem testes.
3. Teste de contexto da aplicacao.

## Comandos de validacao (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q -Dtest=BackendApplicationTests test
```

Observacao: se o teste de contexto falhar com `Connection to localhost:5433 refused`, o problema e de infraestrutura local (banco indisponivel), nao de compilacao dos UseCases.

## Proximo foco recomendado (Fase 3)

1. Cobrir UseCases com testes unitarios por modulo (`user`, `auth`, `training`, `exercise`, `dashboard`).
2. Remover services legados sem referencia em controllers.
3. Revisar ports/interfaces para reduzir acoplamento e facilitar manutencao.
4. Manter `SOLID_REFACTORING_STATUS.md` como fonte de verdade do estado atual.
