# GymApp Backend - AI Agent Guidelines (Atualizado)

## Quick Context

- Linguagem: Java 17
- Framework: Spring Boot 4.0.2
- Build: Maven Wrapper (`mvnw.cmd` no Windows)
- Banco local: PostgreSQL em `localhost:5433` (`docker-compose.yml`)
- Perfil padrao local: `dev`

## Arquitetura em uso

Fluxo principal:

`Controller -> UseCase -> Repository/Port -> Entity -> Mapper -> DTO`

Estado atual:
- Fase 2 do SOLID concluida no fluxo HTTP.
- Fase 3 concluida (migracao repository -> port consolidada nos UseCases principais).
- Controllers principais nao dependem mais de `*Service` para regras de dominio.
- Services legados nao devem ser reintroduzidos no fluxo HTTP.

## Regras obrigatorias para novos changes

1. Um UseCase = uma responsabilidade.
2. Um unico metodo publico por UseCase: `execute(...)`.
3. Read: `@Transactional(readOnly = true)`.
4. Write: `@Transactional`.
5. `log.info(...)` no inicio do `execute(...)`.
6. Controller apenas orquestra HTTP (sem regra de negocio complexa).
7. API responde DTO, nao entidade.

## Convencoes de nomenclatura

- UseCase: `{Verb}{Noun}UseCase` (ex.: `GetUserByIdUseCase`, `UpdateProgramUseCase`).
- Evite nomes ambiguos; padronize verbo (`Get`, `List`, `Create`, `Update`, `Delete`, `Activate`, `Deactivate`, `Reorder`).

## Pontos criticos ja encontrados no projeto

### 1) Conflito de bean name entre modulos

Existem UseCases com mesmo nome em `training` e `exercise` (ex.: `CreateExerciseUseCase`).
Quando houver duplicidade cross-modulo, use nome explicito no `@Service("...")`.

Exemplo ja aplicado:

```java
@Service("trainingCreateExerciseUseCase")
public class CreateExerciseUseCase { ... }

@Service("exerciseCreateExerciseUseCase")
public class CreateExerciseUseCase { ... }
```

### 2) Methods derivadas do Spring Data

Respeite naming convention estrita para query derivada.

Exemplo valido:

```java
boolean existsByTrainingSheetId(Long sheetId);
```

## Exemplo de UseCase alinhado ao estado atual

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class ReorderSheetUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetCommandPort commandPort;
    private final TrainingSheetMapper mapper;

    @Transactional
    public TrainingSheetResponse execute(Long sheetId, Integer newOrder) {
        log.info("Alterando ordem da folha {} para {}", sheetId, newOrder);

        if (newOrder == null || newOrder < 1) {
            throw new BadRequestException("Ordem deve ser maior que 0");
        }

        TrainingSheet sheet = queryPort.findById(sheetId)
            .orElseThrow(() -> new BadRequestException("Ficha de treino nao encontrada"));

        sheet.setOrderInProgram(newOrder);
        TrainingSheet saved = commandPort.update(sheet);
        return mapper.toResponse(saved);
    }
}
```

## Modulos e estado atual

- `user`: concluido em UseCases no controller.
- `auth`: concluido em UseCases no controller.
- `training`: concluido em UseCases no controller, incluindo reorder.
- `exercise`: concluido em UseCases no controller.
- `dashboard`: concluido em UseCases no controller.

## O que priorizar depois da Fase 3

1. Planejar e executar Fase 4 (OCP), focando extensibilidade sem quebrar contratos.
2. Consolidar backlog de melhoria continua (testes negativos adicionais, pequenos ajustes de fronteira).
3. Manter padrao UseCase + Port para novos fluxos.

## Comandos de validacao (Windows PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q "-Dtest=backend.user.usecase.*Test,backend.auth.usecase.*Test,backend.dashboard.usecase.*Test,backend.training.usecase.*Test,backend.exercise.usecase.*Test" test
```

## Referencias internas

- Status atual: `SOLID_REFACTORING_STATUS.md`
- Navegacao de docs: `DOCUMENTATION_INDEX.md`
