# GymApp - Sistema de Gestão de Academia

## Visão Geral

GymApp é um sistema completo de gestão para academias e profissionais de educação física. Permite o gerenciamento de alunos, planos de treino, exercícios, pagamentos e muito mais.

## Tecnologias

### Backend
- **Java 17** + Spring Boot 4.0.4
- **Banco de dados**: PostgreSQL (Docker)
- **Arquitetura**: Hexagonal (Ports & Adapters) com UseCases
- **Gerenciador**: Maven

### Frontend
- **Next.js 16** (App Router)
- **React 19** + TypeScript
- **Tailwind CSS 4** + shadcn/ui
- **Estado**: Zustand + TanStack React Query

## Variáveis de Ambiente

### Backend

O backend usa **profile ativo `dev`** que é configurado via linha de comando ou variavel de ambiente. Configure antes de rodar:

```bash
# via命令行 (descomente no mvnw.cmd ou docker-compose)
SPRING_PROFILES_ACTIVE=dev
```

O banco de dados é iniciado automaticamente via `docker-compose.yml`:

```yaml
# backend/docker-compose.yml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: gym_dev
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5433:5432"
  mailhog:
    image: mailhog/mailhog:v1.0.1
    ports:
      - "1025:1025"  # SMTP
      - "8025:8025"  # Web UI
```

Para customize, create `backend/src/main/resources/application-dev.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/gym_dev
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

app:
  host: http://localhost:8080
  jwt:
    secret: your-256-bit-secret-key-here-minimum-32-chars
    expiration-ms: 3600000
    refresh-expiration-ms: 604800000
  mail:
    host: localhost
    port: 1025
```

### Frontend

```env
# .env.local (desenvolvimento)
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# .env.production (produção)
NEXT_PUBLIC_API_URL=https://seu-backend-production.com/api
```

## Como Executar

### Pré-requisitos
- Docker + Docker Compose
- Java 17

### Backend

```bash
cd backend
docker compose up -d        # Inicia PostgreSQL na porta 5433
./mvnw.cmd -q clean compile  # Compila o projeto
./mvnw.cmd spring-boot:run  # Inicia na porta 8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev                  # Inicia na porta 3000
```

## Estrutura de Rotas

### Backend (API REST)

| Prefixo | Módulo |
|--------|--------|
| `/api/auth` | Autenticação (login, register, refresh, logout, password reset) |
| `/api/users` | Gestão de usuários |
| `/api/plans` | Planos de assinatura |
| `/api/subscriptions` | Assinaturas |
| `/api/payments` | Pagamentos |
| `/api/training` | Treinos, programas e fichas |
| `/api/exercises` | Banco de exercícios |
| `/api/dashboard` | Dashboards |

### Frontend

| Rota | Descrição |
|------|----------|
| `/auth` | Login / Registro |
| `/forgot-password` | Esqueci a senha |
| `/reset-password?token=` | Redefinir senha |
| `/verify-email?token=` | Verificar email |
| `/dashboard` | Dashboard principal |
| `/plans` | Planos |
| `/subscriptions` | Assinaturas |
| `/payments` | Pagamentos |
| `/training-programs` | Programas de treino |
| `/training-sheets` | Fichas de treino |
| `/training-exercises` | Exercícios na ficha |
| `/exercises` | Banco de exercícios |
| `/exercises/categories` | Categorias de exercícios |
| `/users` | Usuários |

## Usuários de Teste

Link do site: https://gym-app-chi-rose.vercel.app/

Todos os usuários abaixo usam a senha: **senha123**

### Administrador
| Email | Nome | Papel |
|-------|------|-------|
| admin@fitapp.com | Admin Sistema | Administrador |

### Personal Trainers
| Email | Nome | Papel |
|-------|------|-------|
| carlos.personal@fitapp.com | Carlos Mendes | PersonalTrainer |
| fernanda.pt@fitapp.com | Fernanda Oliveira | PersonalTrainer |
| rafael.pt@fitapp.com | Rafael Torres | PersonalTrainer |

### Alunos
| Email | Nome |
|-------|------|
| lucas@aluno.com | Lucas Ferreira |
| mariana@aluno.com | Mariana Costa |
| pedro@aluno.com | Pedro Alves |
| juliana@aluno.com | Juliana Ramos |
| bruno@aluno.com | Bruno Souza |
| camila@aluno.com | Camila Nunes |
| thiago@aluno.com | Thiago Lima |
| isabela@aluno.com | Isabela Martins |
| diego@aluno.com | Diego Campos |

## Documentação Adicional

### Backend
- `backend/AGENTS.md` - Guia para desenvolvedores
- `backend/SOLID_PHASE_*_*.md` - Histórico de refatoração

### Frontend
- Estrutura basada em convensão Next.js App Router
- Componentes em `src/components/`
- Pages em `src/app/`
- Serviços e hooks em `src/services/` e `src/hooks/`

## Screenshots e Funcionalidades

O sistema inclui:
- Dashboard admin e student
- Gestão de planos e assinaturas
- Controle de pagamentos
- Programa de treino por aluno
- Banco de exercícios com categorias
- Edição visual de fichas de treino