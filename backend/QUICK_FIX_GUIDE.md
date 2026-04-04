# 🚀 QUICK FIX GUIDE - O Que Fazer Diferente Daqui em Diante

> Observacao: use este guia como apoio. As regras vigentes da Fase 2 estao em `AGENTS.md` e `SOLID_REFACTORING_STATUS.md`.
> Atualizacao Mar/2026: varios exemplos abaixo usam `Repository` para ilustrar padroes da Fase 2. Para implementacoes atuais, prefira injecao por `*Port` nos UseCases.

## 📋 Checklist para Novos UseCases

Quando criar novos UseCases, siga este checklist para evitar os mesmos erros:

### ✅ 1. Estrutura Básica

```java
@Service
@Slf4j
public class [VerbNoun]UseCase {
    
    // ✅ Campos na ordem lógica:
    // 1. Repositórios (principal)
    // 2. Repositórios (suporte)
    // 3. Mappers/Helpers
    private final [Repository] repository;
    private final [Helper] helper;
    private final [Mapper] mapper;

    // ✅ Constructor
    public [VerbNoun]UseCase([Repository] repository, [Helper] helper, [Mapper] mapper) {
        this.repository = repository;
        this.helper = helper;
        this.mapper = mapper;
    }

    // ✅ Um único método público: execute()
    @Transactional  // ✅ SEMPRE para writes, NUNCA esquecer!
    // @Transactional(readOnly = true) para leitura
    public [ReturnType] execute([Parameters]) {
        log.info("Iniciando [operação]: {}", [key_param]);
        
        // ... lógica aqui ...
        
        log.info("[Operação] com sucesso: {}", [key_param]);
        return [result];
    }
}
```

### ✅ 2. Validação de Null (ORDER MATTERS!)

```java
// ❌ ERRADO - ValueObject ANTES de null check
Email email = new Email(userRequest.getEmail());  // BOOM se null!
if(userRequest.getEmail() != null) { ... }

// ✅ CORRETO - Null check PRIMEIRO
if(userRequest.getEmail() != null) {
    Email email = new Email(userRequest.getEmail());  // Seguro
    // ... lógica de validação ...
}
```

### ✅ 3. Injeções de Dependência

```java
// ❌ ERRADO - Injetar o que não usa
public UpdateUserUseCase(UserRepository repo, 
                         RoleRepository role,
                         UserMapper mapper,
                         JwtTokenProvider jwt,    // ❌ Nunca usado!
                         EmailService email) {    // ❌ Nunca usado!
    this.repo = repo;
    this.role = role;
    this.mapper = mapper;
}

// ✅ CORRETO - Apenas o necessário
public UpdateUserUseCase(UserRepository repo, 
                         RoleRepository role,
                         UserMapper mapper) {
    this.repo = repo;
    this.role = role;
    this.mapper = mapper;
}
```

### ✅ 4. Logging Strategy

```java
@Transactional
public UserResponse execute(Long userId, UserUpdateRequest request) {
    // ✅ SEMPRE: log na entrada com parâmetros chave
    log.info("Iniciando atualização de usuário: {}", userId);
    
    // Validações...
    User user = repository.findById(userId)
        .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
    
    // ✅ ADICIONAL: log.debug() para debugging
    log.debug("Validando email: {}", request.getEmail());
    
    if(request.getEmail() != null) {
        Email email = new Email(request.getEmail());
        log.debug("Email validado com sucesso");
    }
    
    // ... mais lógica ...
    
    repository.save(user);
    
    // ✅ SEMPRE: log de sucesso
    log.info("Usuário {} atualizado com sucesso", userId);
    
    return mapper.toResponse(user);
}
```

---

## 🎯 Ordem de Fazer as Coisas (Template)

### Passo 1: Validar Null de Parâmetros
```java
// Se o UseCase recebe DTO:
if(request.getEmail() != null) {
    // Criar ValueObject
}
```

### Passo 2: Buscar/Carregar Entidades
```java
User user = repository.findById(id)
    .orElseThrow(() -> new BadRequestException("Não encontrado"));
```

### Passo 3: Validar Regras de Negócio
```java
if(email já existe) {
    throw new BadRequestException("Email já cadastrado");
}
```

### Passo 4: Atualizar Campos
```java
user.setEmail(email);
user.setName(request.getName());
```

### Passo 5: Persistir
```java
repository.save(user);
```

### Passo 6: Log de Sucesso + Retornar
```java
log.info("Usuário {} atualizado com sucesso", id);
return mapper.toResponse(user);
```

---

## 📚 Exemplos Prontos para Copiar

### Exemplo 1: UseCase de Leitura (ReadOnly)

```java
@Service
@Slf4j
public class GetUserByIdUseCase {
    private final UserRepository repository;
    private final UserMapper mapper;

    public GetUserByIdUseCase(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)  // ✅ ReadOnly para queries
    public UserResponse execute(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        User user = repository.findById(id)
            .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        log.debug("Usuário encontrado: {}", user.getId());
        return mapper.toResponse(user);
    }
}
```

### Exemplo 2: UseCase de Escrita (Write)

```java
@Service
@Slf4j
public class CreateUserUseCase {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;

    public CreateUserUseCase(UserRepository repository, RoleRepository roleRepository, UserMapper mapper) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @Transactional  // ✅ SEMPRE para writes
    public UserResponse execute(CreateUserRequest request) {
        log.info("Criando novo usuário: {}", request.getEmail());
        
        // Step 1: Validar email único
        if(repository.existsByEmail(new Email(request.getEmail()))) {
            throw new BadRequestException("Email já cadastrado");
        }
        
        // Step 2: Buscar role
        Role role = roleRepository.findById(request.getRoleId())
            .orElseThrow(() -> new BadRequestException("Role inválida"));
        
        // Step 3: Criar usuário
        User user = User.builder()
            .email(new Email(request.getEmail()))
            .name(request.getName())
            .cpf(new Cpf(request.getCpf()))
            .role(role)
            .active(true)
            .build();
        
        // Step 4: Persistir
        user = repository.save(user);
        
        log.info("Usuário criado com sucesso: {}", user.getId());
        return mapper.toResponse(user);
    }
}
```

### Exemplo 3: UseCase com Filtro (Role-based)

```java
@Service
@Slf4j
public class ListUsersUseCase {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final JwtTokenProvider tokenProvider;

    public ListUsersUseCase(UserRepository repository, UserMapper mapper, JwtTokenProvider tokenProvider) {
        this.repository = repository;
        this.mapper = mapper;
        this.tokenProvider = tokenProvider;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> execute(Authentication authentication) {
        log.info("Listando usuários...");
        String roleFromToken = tokenProvider.getRoleFromToken((String) authentication.getCredentials());
        
        log.debug("Filtrando por role: {}", roleFromToken);

        List<User> users = roleFromToken.equals(Roles.ALUNO.getRole())
            ? repository.findByRole(Roles.PERSONAL.getRole())
            : repository.findAll();
        
        log.debug("Total de usuários: {}", users.size());
        return users.stream()
            .map(mapper::toResponse)
            .toList();
    }
}
```

---

## 🚫 Anti-Patterns: O Que NÃO Fazer

### ❌ 1. Sem @Transactional em Operações Write

```java
// ❌ ERRADO:
public void deactivateUser(Long id) {
    User user = repository.findById(id).orElseThrow(...);
    user.setActive(false);
    repository.save(user);  // Sem transação!
}

// ✅ CORRETO:
@Transactional
public void deactivateUser(Long id) {
    User user = repository.findById(id).orElseThrow(...);
    user.setActive(false);
    repository.save(user);  // Com transação garantida
}
```

### ❌ 2. Validação de Null Depois de Criar ValueObject

```java
// ❌ ERRADO:
Email email = new Email(request.getEmail());  // Falha se null
if(request.getEmail() != null) { ... }        // Muito tarde

// ✅ CORRETO:
if(request.getEmail() != null) {
    Email email = new Email(request.getEmail());  // Seguro
    // ...
}
```

### ❌ 3. Injetar Dependências Não Utilizadas

```java
// ❌ ERRADO:
public MyUseCase(Repo repo, Service svc, Other other, 
                 JwtProvider jwt, Cache cache, Logger log) {
    // Alguns nunca usamos!
}

// ✅ CORRETO:
public MyUseCase(Repo repo, Service svc, Other other) {
    // Apenas o necessário
}
```

### ❌ 4. UseCase com Múltiplas Responsabilidades

```java
// ❌ ERRADO:
@Service
public class UserManagementUseCase {
    public UserResponse createUser(...) { ... }      // Responsabilidade 1
    public UserResponse updateUser(...) { ... }      // Responsabilidade 2
    public void deleteUser(...) { ... }              // Responsabilidade 3
    public void sendWelcomeEmail(...) { ... }        // Responsabilidade 4
}

// ✅ CORRETO: Separar em UseCases individuais
@Service
public class CreateUserUseCase { ... }
@Service
public class UpdateUserUseCase { ... }
@Service
public class DeleteUserUseCase { ... }
@Service
public class SendWelcomeEmailUseCase { ... }
```

### ❌ 5. UseCase Chamando Outro UseCase

```java
// ❌ ERRADO:
@Service
public class CreateUserAndSendEmailUseCase {
    private final CreateUserUseCase createUser;
    private final SendEmailUseCase sendEmail;
    
    public void execute(...) {
        UserResponse user = createUser.execute(...);      // ❌ UseCase chamando UseCase
        sendEmail.execute(user.getEmail(), ...);          // ❌ UseCase chamando UseCase
    }
}

// ✅ CORRETO: Deixar o Controller orquestrar
@RestController
public class UserController {
    private final CreateUserUseCase createUser;
    private final SendEmailUseCase sendEmail;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest req) {
        UserResponse user = createUser.execute(req);      // ✅ Controller orquestra
        sendEmail.execute(user.getEmail(), ...);          // ✅ Controller orquestra
        return ResponseEntity.ok(user);
    }
}
```

---

## 📋 Controller Integration

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class UserController {
    
    // ✅ Injetar TODOS os UseCases necessários
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUserUseCase listUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getUserByIdUseCase.execute(id));  // ✅ Simplificar: apenas chamar
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list(Authentication auth) {
        return ResponseEntity.ok(listUserUseCase.execute(auth));  // ✅ Passar authentication
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        return ResponseEntity.status(201).body(createUserUseCase.execute(req));  // ✅ 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, 
                                               @Valid @RequestBody UpdateUserRequest req) {
        return ResponseEntity.ok(updateUserUseCase.execute(id, req));  // ✅ Simples
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();  // ✅ 204 No Content
    }
}
```

---

## 🎓 Lições Aprendidas

| Erro | Lesson | Ação |
|------|--------|------|
| Sem @Transactional | Write operations precisam de transação | Sempre adicionar em métodos que mudam dados |
| Validar null depois | Order importa em programação | Sempre validar ANTES de usar |
| Injetar não utilizado | Menos é mais | Injetar apenas o necessário |
| Múltiplas responsabilidades | SRP: Uma coisa bem feita | Um UseCase = uma responsabilidade |
| UseCase → UseCase | Orquestração no Controller | Controller coordena, UseCases executam |
| Sem logging sucesso | Auditoria completa | Sempre log na entrada E na saída |

---

## 📞 Quando Copiar-e-Colar

✅ **Use estes templates:**
- Estrutura do UseCase (com @Transactional)
- Ordem de validações (null check primeiro)
- Padrão de logging (entrada, debug, sucesso)
- Padrão de error handling (BadRequestException, etc)

❌ **NÃO copie:**
- Lógica de negócio específica (cada UseCase é diferente)
- Repositories específicas (cada domínio tem suas)
- Campos específicos de cada entidade

---

## 🚀 Próximo UseCase? 

Siga este passo-a-passo:

1. [ ] Criar classe: `src/main/java/backend/[module]/usecase/[VerbNoun]UseCase.java`
2. [ ] Adicionar `@Service @Slf4j` e imports
3. [ ] Declarar campos (repo, helper, mapper) em ordem lógica
4. [ ] Constructor padrão (injetar apenas necessário)
5. [ ] Um método `execute()` com `@Transactional`
6. [ ] Implementar com 7 padrões:
   - Validar null ANTES de ValueObject
   - Buscar entidades necessárias
   - Validar regras de negócio
   - Atualizar/Criar campos
   - Persistir (repository.save)
   - Log de sucesso
   - Retornar DTO
7. [ ] Compilar: `./mvnw compile`
8. [ ] Injetar no Controller
9. [ ] Pronto! 🎉


