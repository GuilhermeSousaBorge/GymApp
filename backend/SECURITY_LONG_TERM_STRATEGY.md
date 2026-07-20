# GymApp - Estrategia de Seguranca de Longo Prazo

## Roadmap de Seguranca (2026)

---

## Q2 2026 (Abril-Junho)

### Entregas do baseline (concluidas)
- [x] Analise de seguranca completa
- [x] Atualizar Spring Boot para 4.0.4
- [x] Proteger GraphQL
- [x] Validar JWT Secret
- [x] Desabilitar/limitar H2 em prod
- [x] OWASP headers em producao
- [x] Rate limiting em `/api/auth/**`

### Curto Prazo (restante de Abril)
- [ ] Logging de auditoria de seguranca
- [ ] Re-auditoria funcional (smoke + endpoints criticos)
- [ ] Metricas de auth/rate-limit (401/403/429)

### Medio Prazo (Maio-Junho)
- [ ] Revisao de codigo de seguranca por modulo
- [ ] Testes de penetracao (escopo API)
- [ ] Plano de resposta a incidentes formalizado
- [ ] Documentacao operacional de seguranca

---

## 🔐 Políticas de Segurança

### 1. Gestão de Secrets

**Princípio**: Nenhum secret em código-fonte

```yaml
# ❌ NUNCA
jwt:
  secret: "minha-senha-super-secreta"

# ✅ SEMPRE
jwt:
  secret: ${JWT_SECRET}  # Variável de ambiente
```

**Ferramentas Recomendadas**:
- HashiCorp Vault
- AWS Secrets Manager
- GitHub Secrets (CI/CD)

**Verificação Regular**:
```bash
# Scan para secrets em git history
git log --all --pretty=format: -S 'password' -S 'secret' -S 'api_key'

# Ferramentas: TruffleHog, Detect Secrets
```

---

### 2. Versionamento de Dependências

**Política**: Manter dependências atualizadas

```bash
# Verificar updates disponíveis:
.\mvnw.cmd versions:display-dependency-updates

# Verificar vulnerabilidades:
.\mvnw.cmd dependency-check:check

# Update automático com OWASP:
.\mvnw.cmd dependency-check:aggregate
```

**Cadência**:
- Security fixes: IMEDIATO (< 24h)
- Minor updates: Mensal
- Major updates: Trimestral + testes extensivos

---

### 3. Code Review de Segurança

**Checklist Obrigatório para PRs**:

```markdown
## Security Review Checklist

- [ ] Nenhum hardcoded secret/password
- [ ] Validação de input implementada
- [ ] Proteção contra SQL injection (JPA)
- [ ] CORS configurado corretamente
- [ ] Autenticação/Autorização verificada
- [ ] Senhas encoded com BCrypt
- [ ] Logging não expõe dados sensíveis
- [ ] Testes de segurança inclusos
- [ ] Documentação atualizada
- [ ] Nenhuma dependência vulnerável
```

---

### 4. Autenticação & Autorização

#### Evolução de Autenticação

```timeline
2026-Q2:
├─ ✅ JWT token-based (ATUAL)
├─ [ ] Refresh tokens
└─ [ ] Token rotation

2026-Q3:
├─ [ ] OAuth2 (opcional)
├─ [ ] OIDC (opcional)
└─ [ ] 2FA (opcional)

2026-Q4:
├─ [ ] SSO integrado
├─ [ ] Session audit logs
└─ [ ] Device fingerprinting
```

#### Segurança de Senha

```java
// Padrão: BCrypt com rounds = 12 (padrão do Spring)
new BCryptPasswordEncoder(12)  // ✅ Seguro

// Validações:
- Mínimo 8 caracteres
- Deve conter: maiúscula, minúscula, número, símbolo
- Não usar último N passwords (histórico)
```

---

### 5. Logging & Auditoria

**Dados que DEVEM ser logados:**
```java
✅ Tentativas de login (sucesso/falha)
✅ Mudanças de dados sensíveis (senha, email)
✅ Acessos a dados protegidos
✅ Mudanças de permissões
✅ Eventos de segurança (failed logins, etc)

❌ NUNCA logar:
❌ Passwords (jamais!)
❌ Tokens JWT
❌ Números de cartão
❌ Dados PII (quando não necessário)
```

**Implementação Recomendada**:
```java
@Aspect
@Component
public class SecurityAuditLogger {
    
    @Before("@annotation(com.example.Auditable)")
    public void auditableAction(JoinPoint jp) {
        User user = getCurrentUser();
        String action = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        
        log.info("AUDIT: User {} performed action {} with args {}",
                 user.getId(), action, sanitizeArgs(args));
    }
    
    private Object[] sanitizeArgs(Object[] args) {
        // Remove passwords, tokens, etc
        return args;
    }
}
```

---

### 6. Testes de Segurança

**Testes Obrigatórios**:

```java
// 1. Teste de autenticação falha
@Test
public void testUnauthorizedAccess() {
    assertThrows(UnauthorizedException.class, 
                 () -> protectedService.execute());
}

// 2. Teste de autorização
@Test
public void testForbiddenAccess() {
    authenticateAs("user_role");
    assertThrows(AccessDeniedException.class,
                 () -> adminService.execute());
}

// 3. Teste de SQL injection
@Test
public void testSqlInjectionPrevention() {
    String malicious = "admin'; DROP TABLE users; --";
    assertTrue(service.findByEmail(malicious).isEmpty());
}

// 4. Teste de CORS
@Test
public void testCorsPolicy() {
    assertFalse(corsAllowsOrigin("https://evil.com"));
    assertTrue(corsAllowsOrigin("https://gym-app-chi-rose.vercel.app"));
}
```

---

## 🔍 Monitoramento de Segurança

### Metricas a Rastrear

```
1. Taxa de falha de autenticação
2. Requisições sem autenticação
3. Acessos a endpoints sensíveis
4. Mudanças de dados críticos
5. Erros 401/403
6. Requisições suspeitas (rate limit violations)
7. Tentativas de SQL injection
8. XSS attempts
```

### Dashboard Recomendado

```plaintext
┌─────────────────────────────────────┐
│   Security Dashboard               │
├─────────────────────────────────────┤
│ Failed Logins (24h):        23      │ ⚠️
│ Unauthorized Access:         5      │ 🔴
│ Rate Limit Violations:      12      │ ⚠️
│ CVE Vulnerabilities:         0      │ ✅
│ Security Headers OK:        Yes     │ ✅
└─────────────────────────────────────┘
```

---

## 🛠️ Ferramentas Recomendadas

### Scanning de Segurança

```bash
# OWASP Dependency Check
.\mvnw.cmd org.owasp:dependency-check-maven:check

# SonarQube (análise de código)
.\mvnw.cmd sonar:sonar

# Snyk (vulnerabilidades de dependências)
snyk test

# Trivy (scanning de container)
trivy image seu-registry/gym-app:latest
```

### Configuração no pom.xml

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
    </configuration>
</plugin>
```

---

## 🚀 Implementação de Pipeline CI/CD Seguro

### GitHub Actions Example

```yaml
name: Security Checks

on: [push, pull_request]

jobs:
  security:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Run OWASP Dependency Check
        run: |
          .\mvnw.cmd org.owasp:dependency-check-maven:check
          
      - name: SonarQube Scan
        run: |
          .\mvnw.cmd sonar:sonar \
            -Dsonar.projectKey=gym-app \
            -Dsonar.host.url=${{ secrets.SONAR_HOST }} \
            -Dsonar.login=${{ secrets.SONAR_TOKEN }}
      
      - name: Run Security Tests
        run: |
          .\mvnw.cmd test -Dgroups=security
      
      - name: Upload Reports
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: security-reports
          path: target/dependency-check-report.*
```

---

## 📋 Compliance & Regulamentações

### Padrões a Atender

```
OWASP Top 10 2023
├─ A01: Broken Access Control ✅
├─ A02: Cryptographic Failures ✅
├─ A03: Injection ✅
├─ A04: Insecure Design ⚠️
├─ A05: Security Misconfiguration 🔧
├─ A06: Vulnerable Components 🔧
├─ A07: Authentication Failures ⚠️
├─ A08: Data Integrity Failures ⚠️
├─ A09: Logging Monitoring Failures ⚠️
└─ A10: SSRF ⚠️

PCI DSS (se processar pagamentos)
├─ Criptografia de dados
├─ Proteção de acesso
├─ Monitoramento
└─ Testes de penetração

GDPR (se usuários EU)
├─ Consentimento
├─ Direito ao esquecimento
└─ Relatórios de data breach
```

---

## 🔄 Ciclo de Auditoria

### Calendário de Revisões

```
Diariamente:
- [ ] Verificar alertas de segurança
- [ ] Revisar logs de erros críticos

Semanalmente:
- [ ] Code review de segurança
- [ ] Verificar dependências atualizadas

Mensalmente:
- [ ] Auditoria completa de logs
- [ ] Testes de penetração manual
- [ ] Review de políticas

Trimestralmente:
- [ ] Auditoria externa
- [ ] Atualização de documentação
- [ ] Treinamento de segurança

Anualmente:
- [ ] Revisão completa de arquitetura
- [ ] Assessment de conformidade
- [ ] Planejamento de roadmap
```

---

## 🎓 Treinamento de Segurança

### Para Desenvolvedores

```
Módulo 1: Fundamentos (2h)
├─ OWASP Top 10
├─ Secure Coding
└─ Threat Modeling

Módulo 2: Prático (4h)
├─ Spring Security
├─ Testes de segurança
└─ Code review

Módulo 3: Avançado (2h)
├─ Criptografia
├─ OAuth2 / OIDC
└─ Incident response
```

### Recursos

- OWASP WebGoat
- Spring Security Academy
- PortSwigger Web Security Academy
- HackTheBox

---

## 🆘 Plano de Resposta a Incidentes

### Procedimento de Segurança

```
1. DETECTAR (Monitor/Alertas)
   └─ Escalate para security team

2. CONTER (Limitar danos)
   ├─ Desativar acessos comprometidos
   ├─ Reverter mudanças suspeitas
   └─ Coletar evidências

3. INVESTIGAR (Root cause)
   ├─ Análise de logs
   ├─ Verificar impacto
   └─ Documentar findings

4. REMEDIAR (Corrigir)
   ├─ Patch vulnerabilidade
   ├─ Deploy seguro
   └─ Validar correção

5. COMUNICAR
   ├─ Notificar usuários (se necessário)
   ├─ Comunicado de segurança
   └─ Post-mortem

6. PREVENIR (Melhorias)
   ├─ Implementar detecções
   ├─ Atualizar documentação
   └─ Treinamento
```

---

## 📞 Contatos de Segurança

```
Security Team Lead: [Nome] - [Email]
DevOps Lead: [Nome] - [Email]
Emergency: [Telefone] - [Email]

External Consultants:
- Security Auditor: [Contato]
- Penetration Tester: [Contato]
```

---

## 📚 Referências

- [OWASP Top 10 2023](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [PCI DSS](https://www.pcisecuritystandards.org/)

---

## Next Review Date

**Data**: 2026-05-05  
**Responsavel**: [Nome]  
**Escopo**: Revisao completa de seguranca e monitoramento

---

**Preparado por**: GitHub Copilot  
**Data**: 2026-04-05  
**Ultima atualizacao**: 2026-04-05  
**Proxima revisao**: 2026-05-05


