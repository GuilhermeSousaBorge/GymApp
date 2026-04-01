# Guia de Arquitetura V2 (Replicavel)

> Ultima atualizacao: 2026-03-31
> Objetivo: transformar o que foi feito no GymApp em um modelo que voce consegue copiar para outros projetos.

## O que esta versao adiciona
- Diagramas textuais (sequencia, modulos e dependencias).
- Template copiavel de estrutura SOLID modular.
- Checklist de PR arquitetural para evitar regressao.

---

## 1) Mapa mental rapido

- Camada HTTP: `controller/`.
- Regra de negocio: `usecase/`.
- Contratos de dependencia: `port/`.
- Implementacoes de infraestrutura: `adapter/` + `repository/`.
- Modelo de dominio: `model/`.
- Contratos de API: `dto/` + `mapper/`.

Fluxo oficial:
`Controller -> UseCase -> Port -> Adapter/Repository -> Entity -> Mapper -> DTO`

---

## 2) Diagrama de sequencia (request de escrita)

```text
Client
  |
  | HTTP POST/PUT/PATCH
  v
Controller
  |-- valida entrada (@Valid) e contexto de auth
  v
UseCase.execute(...)
  |-- log.info inicio
  |-- validacoes de regra (negocio)
  |-- chama QueryPort/ValidationPort
  |-- aplica mudanca
  |-- chama CommandPort
  v
Adapter
  |-- traduz porta para Repository
  v
Repository (JPA)
  |-- persistencia
  v
Entity atualizada
  |
  v
Mapper -> Response DTO
  |
  v
Controller retorna HTTP status + DTO
```

### Regra de ouro
- Controller nao decide negocio.
- UseCase nao conhece detalhes de persistence.

---

## 3) Diagrama de sequencia (request de leitura)

```text
Client -> Controller (GET)
Controller -> UseCase.execute(...)
UseCase -> QueryPort
QueryPort -> Adapter -> Repository
Repository -> Entity
UseCase -> Mapper -> DTO
Controller -> Client (200 + DTO)
```

### Regras esperadas
- UseCase de leitura com `@Transactional(readOnly = true)`.
- Sem retorno de entidade direto na API.

---

## 4) Diagrama de modulos do GymApp (estado atual)

```text
                   +----------------+
                   |  infrastructure|
                   | (security/exc) |
                   +--------+-------+
                            |
+---------+  +---------+  +---------+  +-----------+
|  auth   |  |  user   |  |dashboard|  |   plan    |
+----+----+  +----+----+  +----+----+  +-----+-----+
     |            |            |              |
     +------------+------------+              |
                  |                           |
              +---v---------------------------v---+
              |        training / exercise        |
              |  (ports cross-module explicitos)  |
              +----------------+-------------------+
                               |
                        +------v------+
                        |subscription |
                        +------+------+
                               |
                        +------v------+
                        |  payment    |
                        +-------------+
```

### Fronteiras importantes
- `User -> Subscription -> Plan`.
- `Payment -> Subscription`.
- Acoplamento cross-modulo por `port` (nao por `repository` direto).

---

## 5) Diagrama de dependencias por camada (regra de direcao)

```text
controller  ---> usecase ---> port ---> adapter ---> repository
      \                                         \
       \---------------------------------> mapper -> dto
model/entity fica no centro do dominio e nao depende de controller
```

### O que nao fazer
- `usecase` importando `*.repository.*`.
- `controller` com regra condicional pesada de dominio.
- `adapter` aplicando regra de negocio.

---

## 6) Template copiavel de projeto SOLID modular

## 6.1 Estrutura de pastas base

```text
src/main/java/com/seuprojeto/
  config/
  infrastructure/
    exception/
    security/
  moduloA/
    controller/
    usecase/
    port/
    adapter/
    repository/
    model/
    mapper/
    dto/
  moduloB/
    controller/
    usecase/
    port/
    adapter/
    repository/
    model/
    mapper/
    dto/
src/main/resources/
  application.yaml
  application-dev.yaml
  db/migration/
src/test/java/com/seuprojeto/
  moduloA/usecase/
  moduloB/usecase/
```

## 6.2 Template de UseCase (copiar e adaptar)

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateThingUseCase {

    private final ThingValidationPort validationPort;
    private final ThingCommandPort commandPort;
    private final ThingMapper mapper;

    @Transactional
    public ThingResponse execute(CreateThingRequest request) {
        log.info("Criando thing com nome={}", request.getName());

        if (validationPort.existsByName(request.getName())) {
            throw new BadRequestException("Thing com este nome ja existe");
        }

        Thing saved = commandPort.save(
            Thing.builder().name(request.getName()).active(true).build()
        );

        log.info("Thing {} criada com status {}", saved.getId(), saved.getActive());
        return mapper.toResponse(saved);
    }
}
```

## 6.3 Template de Controller (copiar e adaptar)

```java
@RestController
@RequestMapping("/api/things")
@RequiredArgsConstructor
@Slf4j
public class ThingController {

    private final CreateThingUseCase createThingUseCase;

    @PostMapping
    public ResponseEntity<ThingResponse> create(@Valid @RequestBody CreateThingRequest request) {
        log.info("POST /api/things - name={}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createThingUseCase.execute(request));
    }
}
```

---

## 7) Template de estrategia OCP (1 nivel)

Use quando tiver variacao de regra por tipo/plano/categoria.

```text
Policy (interface)
  |- BasicPolicy
  |- PremiumPolicy
  |- EnterprisePolicy
PolicyResolver
UseCase -> resolve(policy) -> aplica limites/beneficios
```

### Regra pratica
- Comece com 1 Strategy + 1 Resolver.
- Nao introduza cadeia de fabricas sem necessidade real.

---

## 8) Checklist de PR arquitetural (copiar no template de PR)

## Arquitetura
- [ ] `controller` so orquestra HTTP/seguranca.
- [ ] regra de negocio esta em `usecase`.
- [ ] `usecase` usa `port`, nao `repository`.
- [ ] response da API usa DTO.

## Confiabilidade
- [ ] `execute(...)` com `log.info` no inicio.
- [ ] leitura com `@Transactional(readOnly = true)`; escrita com `@Transactional`.
- [ ] transicoes de status invalidas estao protegidas.
- [ ] erros de dominio retornam mensagens claras e consistentes.

## Testes
- [ ] teste de fluxo feliz no UseCase alterado.
- [ ] ao menos 1 teste negativo relevante (nao encontrado/regra/status/autorizacao).
- [ ] mocks em portas, nao em repository (quando o UseCase ja depende de port).

## Documentacao
- [ ] atualizou docs de status/plano/summary se houve mudanca arquitetural.
- [ ] registrou qualquer nova fronteira entre modulos.

---

## 9) Roadmap de replicacao em 6 passos (projeto novo)

1. Criar estrutura modular por dominio (pastas do item 6.1).
2. Definir 2-3 fluxos principais com UseCases simples.
3. Introduzir `port`/`adapter` para todos os novos UseCases.
4. Adotar Flyway desde V1 e `ddl-auto: validate`.
5. Cobrir cada UseCase com teste feliz + negativo.
6. Criar docs vivas: `STATUS`, `PLAN`, `SUMMARY`, `AGENTS`.

---

## 10) Referencias diretas no GymApp

- `AGENTS.md`
- `GUIA_ARQUITETURA_PROJETO.md`
- `SOLID_REFACTORING_STATUS.md`
- `SOLID_PHASE_4_PLAN.md`
- `SOLID_PHASE_4_CONTINUITY.md`
- `SOLID_PHASE_4_SUMMARY.md`
- `src/main/java/backend/plan/policy/PlanPolicy.java`
- `src/main/java/backend/plan/policy/PlanPolicyResolver.java`

---

## Conclusao

Esta V2 te da o que faltava para replicar rapido: mapa de execucao, padrao de pastas, templates de codigo e checklist de PR.
Se voce seguir esses blocos, consegue reproduzir o mesmo estilo arquitetural em qualquer backend Spring modular com muito menos retrabalho.

