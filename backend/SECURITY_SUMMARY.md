# GymApp Security - Resumo Executivo

## TL;DR

**Status geral**: ✅ **BASELINE DE SEGURANCA CONCLUIDO**

```
Implementado:
├── CVEs criticas mitigadas (Spring Boot 4.0.4)
├── GraphQL protegido
├── JWT hardening (validacao + API moderna)
├── H2 bloqueado em prod
├── OWASP headers em prod
└── Rate limiting em /api/auth/**
```

---

## Estado Atual

- Spring Boot atualizado para `4.0.4`
- JJWT alinhado em `0.12.6` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- `JwtTokenProvider` com `verifyWith(SecretKey)` e validacao de secret >= 32 bytes
- `/graphql` e demais rotas sensiveis exigem autenticacao
- `/h2-console/**` permitido apenas em `dev` e negado em outros perfis
- Headers de seguranca aplicados em producao: CSP, HSTS, Content-Type Options, Cache-Control
- Rate limiting com Bucket4j em `/api/auth/**` (5 req/min por IP, HTTP 429)

---

## Itens Pendentes

Nenhum item critico/alto pendente no baseline implementado.

Backlog opcional (melhoria continua):
- Auditoria estruturada de eventos de seguranca
- Dashboard de metricas (401/403/429 por endpoint)
- Revisao periodica dos limites de rate limiting

---

## Validacao Rapida

```powershell
Set-Location "C:\Users\Guilherme\Desktop\Workspace\GymApp\backend"
.\mvnw.cmd -q clean -DskipTests compile
.\mvnw.cmd -q test
```

```powershell
# GraphQL sem token (esperado: 401)
curl -i -X POST http://localhost:8080/graphql -H "Content-Type: application/json" -d "{\"query\":\"{__typename}\"}"

# Burst em auth (esperado: 429 na 6a tentativa dentro de 1 min)
for ($i=1; $i -le 6; $i++) {
  curl -s -o $null -w "%{http_code}`n" -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test@test.com\",\"password\":\"wrong\"}"
}
```

---

**Preparado**: 2026-04-05  
**Ultima atualizacao**: 2026-04-05  
**Proxima revisao**: 2026-05-05


