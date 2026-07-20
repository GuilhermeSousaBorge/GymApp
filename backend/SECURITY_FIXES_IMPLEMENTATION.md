# GymApp Backend - Registro de Implementacao de Seguranca

## Status

Implementacao concluida para todos os itens do baseline de seguranca definidos na auditoria.

---

## Itens Implementados

### 1) Spring Boot atualizado
- Arquivo: `pom.xml`
- Estado: `4.0.4`
- Resultado: mitigacao das CVEs do Actuator citadas na auditoria

### 2) JWT hardening
- Arquivo: `src/main/java/backend/infrastructure/security/JwtTokenProvider.java`
- Implementado:
  - validacao de secret em `@PostConstruct` (null/blank e minimo 32 bytes)
  - API JJWT moderna (`verifyWith`, `parseSignedClaims`, `SecretKey`)

### 3) GraphQL protegido
- Arquivo: `src/main/java/backend/config/SecurityConfig.java`
- Implementado:
  - `/graphql` nao esta em `permitAll()`
  - `anyRequest().authenticated()` protege endpoints nao publicos

### 4) H2 restrito por perfil
- Arquivos:
  - `src/main/resources/application-prod.yaml`
  - `src/main/java/backend/config/SecurityConfig.java`
- Implementado:
  - `spring.h2.console.enabled=false` em prod
  - `/h2-console/**` permitido apenas em `dev` e negado fora de `dev`

### 5) Frame options por perfil
- Arquivo: `src/main/java/backend/config/SecurityConfig.java`
- Implementado:
  - `dev`: `frameOptions.disable()`
  - `prod`: `frameOptions.sameOrigin()`

### 6) OWASP headers em prod
- Arquivo: `src/main/java/backend/config/SecurityConfig.java`
- Implementado:
  - `Content-Security-Policy`
  - `Strict-Transport-Security`
  - `X-Content-Type-Options`
  - `Cache-Control`

### 7) Rate limiting anti brute force
- Arquivos:
  - `pom.xml`
  - `src/main/java/backend/infrastructure/security/RateLimitingFilter.java`
- Implementado:
  - Bucket4j (`bucket4j-core`)
  - limite de `5 req/min por IP` em `/api/auth/**`
  - retorno `HTTP 429` quando excede

### 8) Perfil default removido do base config
- Arquivo: `src/main/resources/application.yaml`
- Implementado:
  - arquivo base vazio, perfil definido externamente (`--spring.profiles.active`)

---

## Validacao Recomendada

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q test
```

```powershell
# GraphQL sem token (esperado 401)
curl -i -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d "{\"query\":\"{__typename}\"}"

# Rate limit (esperado 429 na 6a tentativa)
for ($i=1; $i -le 6; $i++) {
  curl -s -o $null -w "%{http_code}`n" -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test@test.com\",\"password\":\"wrong\"}"
}
```

---

**Ultima atualizacao**: 2026-04-05


