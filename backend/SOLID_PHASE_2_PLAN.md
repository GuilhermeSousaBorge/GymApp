# SOLID Phase 2 Plan (Estado Atual)

## Objetivo da fase

Concluir a migracao para o padrao UseCase, removendo dependencia de `*Service` nos controllers para regras de dominio.

## Contexto atual confirmado no codigo

- UseCases existem em todos os modulos principais.
- `user` ja opera majoritariamente por UseCases.
- `auth`, `training`, `exercise`, `dashboard` ainda estao hibridos (UseCases + Service).
- Houve conflitos de bean name entre UseCases duplicados (`training` x `exercise`), ja mitigados com `@Service("...")`.

## Escopo da conclusao da Fase 2

1. Controller sem regra de negocio.
2. Endpoint atendido por UseCase dedicado.
3. UseCase com unico metodo publico `execute(...)`.
4. Padrao transacional e de logging aplicado.

## Backlog tecnico por modulo

### `training` (prioridade 1)

- Criar UseCase para reorder de sheet.
- Criar UseCase para reorder de exercise em sheet.
- Atualizar `TrainingSheetController` e `TrainingExerciseController` para remover chamadas diretas a service.

### `exercise` (prioridade 2)

- Remover injeções de `ExerciseService` e `ExerciseCategoryService` nos controllers.
- Garantir que todos os fluxos usados em endpoint tenham UseCase.

### `auth` (prioridade 3)

- Remover `AuthService` de `AuthController` se nao houver uso real em endpoint.
- Manter apenas `LoginUseCase`, `RegisterUseCase`, `MeUseCase`, `ValidateTokenUseCase`.

### `dashboard` (prioridade 4)

- Remover `DashboardService` de `DashboardController` se os endpoints ja estiverem totalmente cobertos por UseCases.

## Padrao minimo para novos UseCases

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class ExampleUseCase {

    private final SomeRepository repository;

    @Transactional
    public Output execute(Input input) {
        log.info("Executando caso de uso X com {}", input);
        // regra de negocio
        return output;
    }
}
```

## Criterios de qualidade

- Query derivada com naming Spring Data correto (`existsBy...`, `findBy...`).
- Sem entidade JPA exposta diretamente no controller.
- Sem regras de negocio implementadas no endpoint.

## Validacao continua (PowerShell)

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q -DskipTests compile
.\mvnw.cmd -q -Dtest=BackendApplicationTests test
```

## Definicao de concluido da Fase 2

- Todos os controllers sem dependencia de `*Service` para regras de dominio.
- Todos os endpoints de dominio cobertos por UseCases.
- Teste de contexto passando de forma consistente.
- Documentacao de status atualizada em `SOLID_REFACTORING_STATUS.md`.

