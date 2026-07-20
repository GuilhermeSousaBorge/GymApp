# GymApp Security - Checklist Final

## Status Geral

Checklist consolidado com base no estado atual do codigo.

---

## Implementacao

- [x] Spring Boot `4.0.4`
- [x] JJWT `0.12.6` alinhado
- [x] Validacao JWT secret (>= 32 bytes)
- [x] GraphQL protegido por autenticacao
- [x] H2 desabilitado em prod (`application-prod.yaml`)
- [x] H2 bloqueado por perfil no `SecurityConfig`
- [x] Frame options por perfil
- [x] OWASP headers em prod (CSP, HSTS, content-type, cache-control)
- [x] Rate limiting em `/api/auth/**` com Bucket4j
- [x] `application.yaml` sem profile default fixo

---

## Verificacao Tecnica

- [ ] `./mvnw.cmd -q clean -DskipTests compile` sem erros
- [ ] `./mvnw.cmd -q test` sem falhas
- [ ] `POST /graphql` sem token retorna `401`
- [ ] 6a tentativa em `/api/auth/login` (em 1 min) retorna `429`
- [ ] `/h2-console/**` bloqueado em perfil `prod`

---

## Evidencias de Arquivo

- `pom.xml`
- `src/main/java/backend/config/SecurityConfig.java`
- `src/main/java/backend/infrastructure/security/JwtTokenProvider.java`
- `src/main/java/backend/infrastructure/security/RateLimitingFilter.java`
- `src/main/resources/application-prod.yaml`
- `src/main/resources/application.yaml`

---

**Ultima atualizacao**: 2026-04-05


