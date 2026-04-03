# SOLID Phase 5 Plan - LSP: Substituibilidade de Contratos (Abr/2026)

## Objetivo da fase

Aplicar o **Liskov Substitution Principle (LSP)** de forma pratica:
qualquer implementacao de `Port` deve poder substituir outra sem quebrar o comportamento esperado de `UseCase`, `Controller`, `DTO` e contrato HTTP.

A fase reforca consistencia comportamental, nao apenas compilacao.

---

## Contexto de entrada (gate de inicio)

- Fase 4 encerrada formalmente (base + hardening A/B/C).
- Fluxo arquitetural consolidado: `Controller -> UseCase -> Port -> Adapter/Repository -> Entity -> Mapper -> DTO`.
- UseCases com padrao obrigatorio preservado (`execute(...)`, `@Transactional`, `log.info(...)`).

---

## Escopo da Fase 5

1. **Substituibilidade de Ports/Adapters**
- Garantir equivalencia de comportamento entre implementacoes de leitura/escrita/validacao.
- Evitar implementacoes que alterem semantica de erros, nulos, listas vazias ou filtros.

2. **Contratos de UseCase estaveis**
- Manter pre-condicoes e pos-condicoes explicitas por fluxo.
- Proibir mudanca de assinatura publica sem justificativa arquitetural.

3. **Compatibilidade externa**
- Preservar contrato de DTO e respostas HTTP dos endpoints existentes.
- Preservar padrao de erro centralizado via `GlobalExceptionHandler`.

4. **Testes orientados a contrato**
- Ampliar testes para provar substituibilidade em modulos criticos (`training`, `exercise`, `plan`, `subscription`, `payment`).

---

## Fora de escopo (nao bloqueia a fase)

- Novas regras avancadas de cobranca (`grace period`, `retry policy`, `estorno` com gateway externo).
- Mudancas de produto que criem novos modulos fora do baseline atual.
- Reescrita de controllers por preferencia tecnica sem ganho de contrato.

---

## Slices de entrega

### Slice 1 - Inventario de contratos substituiveis
- [ ] Mapear por modulo os contratos de `Port` com seus comportamentos obrigatorios.
- [ ] Documentar invariantes minimos (erro esperado, retorno vazio, ordenacao, transicoes de status).

### Slice 2 - Hardening de pre/pós-condicoes
- [ ] Padronizar pre-condicoes de entrada em UseCases sensiveis.
- [ ] Padronizar pos-condicoes para evitar divergencia entre adapters.

### Slice 3 - Testes de substituicao por modulo
- [ ] Adicionar/ajustar testes para cenarios equivalentes entre implementacoes.
- [ ] Cobrir ao menos fluxos de leitura/escrita com maior risco de regressao.

### Slice 4 - Fechamento e decisao de fase
- [ ] Consolidar evidencias tecnicas no status global.
- [ ] Publicar resumo oficial da fase (`SOLID_PHASE_5_SUMMARY.md`).

---

## Definicao de concluido (DoD)

1. Contratos de `Port` criticos mapeados com invariantes por modulo.
2. Testes de UseCase cobrindo substituibilidade em fluxos principais.
3. Sem regressao de contrato HTTP/DTO nos endpoints existentes.
4. Compilacao limpa e suites de UseCase verdes.
5. Documentacao sincronizada: plano, resumo e status global.

---

## Go / No-Go da Fase 5

**Go** quando todos os itens abaixo forem verdadeiros:
- [ ] `clean -DskipTests compile` sem erro.
- [ ] Suites de UseCase por modulo sem erro.
- [ ] Nenhuma quebra de contrato publico (UseCase/DTO/HTTP) sem migracao documentada.
- [ ] `SOLID_REFACTORING_STATUS.md` atualizado com evidencias desta fase.

**No-Go** se houver:
- regressao de comportamento entre implementacoes de `Port`;
- divergencia de regra de negocio por adapter;
- mudanca de payload/erro HTTP sem controle de compatibilidade.

---

## Comandos de validacao (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test,backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

---

## Referencias

- `AGENTS.md`
- `SOLID_REFACTORING_STATUS.md`
- `SOLID_PHASE_4_PLAN.md`
- `SOLID_PHASE_4_SUMMARY.md`
- `SOLID_PHASE_4_CONTINUITY.md`

---

## Ultima atualizacao

- Data: 2026-04-02
- Status: Planejado (pronto para inicio)

