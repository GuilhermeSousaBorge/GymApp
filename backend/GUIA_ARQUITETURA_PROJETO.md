# Guia Completo de Arquitetura do Projeto (GymApp Backend)

> Ultima atualizacao: 2026-03-31
> Escopo: estrutura, arquitetura, decisoes tecnicas e como replicar o padrao em projetos futuros.

---

## 1) Visao geral

Este backend foi evoluido por fases de refatoracao SOLID e hoje segue um fluxo arquitetural padronizado:

`Controller -> UseCase -> Port -> Adapter/Repository -> Entity -> Mapper -> DTO`

### Objetivo do desenho
- Isolar regra de negocio em `usecase/`.
- Reduzir acoplamento com persistencia via `port/` (DIP).
- Facilitar extensao de regras sem quebrar contratos existentes (OCP na Fase 4).
- Melhorar testabilidade: testes focam em UseCases com mocks de portas.

---

## 2) Estrutura do repositorio (root)

- `pom.xml`: manifesto Maven (Java 17, Spring Boot 4.0.2, starters web/jpa/security/flyway/graphql/validation, JJWT, OpenAPI).
- `docker-compose.yml`: PostgreSQL local em `localhost:5433`.
- `mvnw` / `mvnw.cmd`: wrapper Maven (padrao para build local/CI).
- `AGENTS.md`: regras praticas para agentes de codigo neste projeto.
- `SOLID_REFACTORING_STATUS.md`: status consolidado oficial das fases.
- `SOLID_PHASE_4_PLAN.md`: plano da Fase 4 (OCP).
- `SOLID_PHASE_4_CONTINUITY.md`: backlog de continuidade (slices).
- `SOLID_PHASE_4_SUMMARY.md`: resumo de entregas da Fase 4.
- `DOCUMENTATION_INDEX.md`: indice de leitura dos documentos principais.

### Por que essa organizacao?
- Separar documento de status (estado real), plano (intencao), summary (snapshot) e continuity (proximos passos).
- Evitar confundir historico com fonte de verdade.

---

## 3) `src/main/java/backend/` (aplicacao)

### Arquivos e pacotes centrais

- `BackendApplication.java`: bootstrap Spring Boot.
- `config/`
  - `SecurityConfig.java`: regras do filtro de seguranca e rotas publicas.
  - `CorsConfig.java`: politica CORS.
  - `PasswordEncoderConfig.java`: encoder de senha (injeção centralizada).
- `infrastructure/`
  - `exception/`: excecoes de dominio + `GlobalExceptionHandler`.
  - `security/`: JWT filter/provider e anotacoes de autorizacao customizadas.

### Modulos de negocio
- `user/`
- `auth/`
- `training/`
- `exercise/`
- `dashboard/`
- `plan/`
- `subscription/`
- `payment/`

Cada modulo segue (com pequenas variacoes):
- `controller/`: endpoints HTTP.
- `usecase/`: regra de aplicacao.
- `port/`: contratos de dependencia.
- `adapter/`: implementacao das portas (ponte para repositorio/infra).
- `repository/`: Spring Data JPA.
- `model/`: entidades e value objects.
- `mapper/`: Entity <-> DTO (quando aplicavel).
- `dto/`: contratos de request/response.
- `service/`: legado/transicao (pastas vazias em modulos principais; nao usar para nova regra).

### Por que modular por dominio?
- Mantem coesao funcional (cada dominio em seu contexto).
- Facilita evolucao incremental por modulo sem quebrar o resto.
- Melhora navegacao e ownership de codigo.

---

## 4) Regras arquiteturais obrigatorias usadas no projeto

1. UseCase possui um unico metodo publico: `execute(...)`.
2. Leitura: `@Transactional(readOnly = true)`.
3. Escrita: `@Transactional`.
4. `log.info(...)` no inicio do `execute(...)`.
5. Controller nao implementa regra de negocio (apenas HTTP + seguranca + orquestracao).
6. UseCase depende de Port, nao de `Repository`.
7. API retorna DTO, nao entidade JPA.

### Excecoes que apareceram no historico e como foram tratadas
- Conflito de nomes de UseCase entre modulos (`training` vs `exercise`):
  - Solucao: `@Service("nomeUnico")`.
- Query derivada Spring Data com nome incorreto:
  - Solucao: respeitar nome exato do campo (ex.: `existsByTrainingSheetId`).

---

## 5) Fluxo de dados (end-to-end)

Exemplo tipico:
1. Controller recebe request validado (`@Valid`).
2. Controller chama `useCase.execute(...)`.
3. UseCase valida regra de negocio e usa portas (`*QueryPort`, `*CommandPort`, etc.).
4. Adapter implementa porta chamando `Repository`.
5. Entidade e persistencia acontecem.
6. Mapper converte entidade para DTO.
7. Controller retorna DTO/HTTP status.

### Beneficio pratico
- Regra de negocio testavel sem subir banco.
- Troca de persistencia minimamente invasiva.
- Menor risco de controller gordo.

---

## 6) Fase 4 (OCP): `plan`, `subscription`, `payment`

### 6.1 Modelo de dominio consolidado
- Relacao de plano: `User -> Subscription -> Plan` (sem `plan_id` direto em `users`).
- Relacao de pagamento: `Payment -> Subscription` (`payments.subscription_id`).

### 6.2 OCP aplicado em planos
- `plan/policy/PlanPolicy.java`: contrato de variacao.
- `plan/policy/PlanPolicyResolver.java`: resolve politica para o plano.
- Novas variacoes devem entrar como nova implementacao de `PlanPolicy` (sem quebrar UseCase existente).

### 6.3 Hardening aplicado
- Slice A: ownership/autorizacao em leituras de assinatura/pagamentos.
- Slice B: testes negativos e validacoes obrigatorias em pagamento.
- Slice C: padronizacao de logs de evento e consistencia de transicoes de status.

---

## 7) Banco de dados e migracoes

Em `src/main/resources/db/migration/`:
- `V1`..`V8`: base inicial (roles, usuarios, exercicios, treino).
- `V10__create_plan_tables_fix.sql`: consolidacao de planos.
- `V11__create_table_subscriptions.sql`: assinaturas.
- `V12__create_table_payments.sql`: pagamentos.

### Por que Flyway + `ddl-auto: validate`?
- Schema versionado por migracao (rastreavel e reproduzivel).
- Hibernate valida, mas nao "inventa" schema em runtime.
- Evita divergencia entre ambiente local e producao.

---

## 8) Configuracoes e ambientes

Em `src/main/resources/`:
- `application.yaml`: profile ativo padrao (`dev`).
- `application-dev.yaml`: datasource local PostgreSQL `localhost:5433`, JWT e logging detalhado.
- `application-prod.yaml`: perfil de producao.

### Decisao importante
- Banco local padrao eh PostgreSQL via Docker, evitando surpresas de dialeto/SQL.

---

## 9) Seguranca

Principais componentes:
- `config/SecurityConfig.java`: `SecurityFilterChain`, rotas liberadas, stateless.
- `infrastructure/security/JwtAuthenticationFilter.java`: extrai token (header/cookie), autentica e injeta principal.
- `infrastructure/security/JwtTokenProvider.java`: geracao/validacao de JWT.
- anotacoes customizadas (`@IsAdmin`, `@IsAdminOrTrainer`, etc.) para regras declarativas.

### Observacao de design
- Autorizacao por ownership foi reforcada em UseCases criticos da Fase 4 para nao depender apenas de anotacao no endpoint.

---

## 10) Testes (`src/test/java/backend/`)

Organizacao por modulo, espelhando `main/java`.
Foco principal: testes unitarios de UseCase com Mockito.

Pontos fortes atuais:
- Cobertura robusta em `user`, `auth`, `dashboard`, `training`, `exercise`.
- Cobertura dedicada Fase 4 em `plan`, `subscription`, `payment`.
- Cenarios negativos relevantes (nao encontrado, status invalido, ownership, validacoes obrigatorias).

### Por que testar no nivel de UseCase?
- Menor custo de manutencao.
- Execucao rapida.
- Valida regra de negocio diretamente.

---

## 11) Rotina de desenvolvimento

### Subir ambiente local
```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
docker compose up -d
```

### Compilar
```powershell
.\mvnw.cmd -q clean -DskipTests compile
```

### Rodar suites principais por modulo
```powershell
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test,backend.plan.usecase.*Test,backend.subscription.usecase.*Test,backend.payment.usecase.*Test" test
```

---

## 12) Como replicar este modelo em outro projeto

1. Defina modulos por dominio (nao por tecnologia).
2. Crie o contrato arquitetural desde o inicio:
   - `controller`, `usecase`, `port`, `adapter`, `repository`, `dto`, `mapper`, `model`.
3. Regra de ouro: toda regra em UseCase; controller apenas orquestra.
4. Introduza `port` cedo para reduzir acoplamento.
5. Versione banco com Flyway desde a primeira tabela.
6. Estabeleca um documento de status oficial (fonte unica) + um plano de continuidade.
7. Garanta testes de UseCase para fluxo feliz e bordas.
8. Para OCP, use um nivel de Strategy antes de considerar fabrica/cadeia.

---

## 13) Arquivos de estudo recomendados (ordem)

1. `AGENTS.md`
2. `SOLID_REFACTORING_STATUS.md`
3. `SOLID_PHASE_4_PLAN.md`
4. `SOLID_PHASE_4_CONTINUITY.md`
5. `src/main/java/backend/plan/controller/PlanController.java`
6. `src/main/java/backend/subscription/usecase/CreateSubscriptionUseCase.java`
7. `src/main/java/backend/payment/usecase/CreatePaymentUseCase.java`
8. `src/main/resources/db/migration/V11__create_table_subscriptions.sql`
9. `src/main/resources/db/migration/V12__create_table_payments.sql`

---

## 14) Conclusao

Este projeto hoje representa uma base pratica para backend modular com SOLID aplicado de forma incremental:
- Fase 2: regras movidas para UseCases.
- Fase 3: dependencias via portas/adapters.
- Fase 4: extensibilidade (OCP), hardening de autorizacao, consistencia de status e observabilidade por logs.

Use este guia como mapa de replicacao: o valor principal aqui nao eh uma tecnologia isolada, e sim a disciplina de fronteiras arquiteturais + evolucao incremental guiada por testes e documentacao viva.

