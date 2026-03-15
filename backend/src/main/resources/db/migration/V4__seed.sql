-- =============================================================================
-- V11__insert_initial_data.sql
-- =============================================================================
-- Popula banco com dados iniciais
-- - Roles e Permissões
-- - Endereço do Admin
-- - Usuário Admin
-- =============================================================================

-- =============================================================================
-- 1. ROLES E PERMISSÕES
-- =============================================================================

-- Role: Administrador
INSERT INTO roles (name, description) VALUES
('Administrador', 'Acesso completo ao sistema');

INSERT INTO role_permissions (role_id, permission) VALUES
(1, 'ADMIN'),
(1, 'USERS_READ'),
(1, 'USERS_WRITE'),
(1, 'TRAINING_MANAGE'),
(1, 'PAYMENTS_READ'),
(1, 'PAYMENTS_MANAGE');

-- Role: Personal Trainer
INSERT INTO roles (name, description) VALUES
('Personal Trainer', 'Pode gerenciar treinos e visualizar alunos');

INSERT INTO role_permissions (role_id, permission) VALUES
(2, 'USERS_READ'),
(2, 'TRAINING_MANAGE'),
(2, 'PAYMENTS_READ');

-- Role: Recepcionista
INSERT INTO roles (name, description) VALUES
('Recepcionista', 'Pode gerenciar pagamentos e visualizar usuários');

INSERT INTO role_permissions (role_id, permission) VALUES
(3, 'USERS_READ'),
(3, 'PAYMENTS_READ'),
(3, 'PAYMENTS_MANAGE');

-- Role: Aluno
INSERT INTO roles (name, description) VALUES
('Aluno', 'Acesso básico do aluno');

-- Aluno não tem permissões especiais (array vazio)

-- =============================================================================
-- 2. ENDEREÇO DO ADMIN
-- =============================================================================

INSERT INTO addresses (number, zip_code, district, street_name, city, state) VALUES
(123, '38300-000', 'Centro', 'Rua das Flores', 'Ituiutaba', 'MG');

-- =============================================================================
-- 3. USUÁRIO ADMIN
-- =============================================================================
-- Senha: admin123
-- Hash BCrypt gerado com: BCryptPasswordEncoder().encode("admin123")

--INSERT INTO users (
--    name,
--    email,
--    password_hash,
--    cpf,
--    phone,
--    gender,
--    birth_date,
--    active,
--    role_id,
--    address_id
--) VALUES (
--    'Administrador',
--    'admin@academia.com',
--    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
--    '123.456.789-00',
--    '(34) 99999-0001',
--    'MALE',
--    '1985-05-15',
--    TRUE,
--    1,  -- Role Administrador
--    1   -- Address
--);

-- =============================================================================
-- 4. CATEGORIAS DE EXERCÍCIOS (Opcional - facilita desenvolvimento)
-- =============================================================================

--INSERT INTO exercise_categories (id, muscle_group, description) VALUES
--(1, 'Peito', 'Exercícios para desenvolvimento peitoral'),
--(2, 'Costas', 'Exercícios para desenvolvimento dorsal'),
--(3, 'Ombros', 'Exercícios para desenvolvimento dos deltoides'),
--(4, 'Bíceps', 'Exercícios para desenvolvimento do bíceps braquial'),
--(5, 'Tríceps', 'Exercícios para desenvolvimento do tríceps braquial'),
--(6, 'Pernas', 'Exercícios para membros inferiores'),
--(7, 'Quadríceps', 'Exercícios focados no quadríceps femoral'),
--(8, 'Posterior de Coxa', 'Exercícios para isquiotibiais'),
--(9, 'Glúteos', 'Exercícios para desenvolvimento glúteo'),
--(10, 'Panturrilha', 'Exercícios para gastrocnêmio e sóleo'),
--(11, 'Abdômen', 'Exercícios para região abdominal'),
--(12, 'Cardio', 'Exercícios cardiovasculares');

-- =============================================================================
-- 5. PLANOS BÁSICOS (Opcional - facilita desenvolvimento)
-- =============================================================================

--INSERT INTO plans (id, name, price, duration_months, benefits, active) VALUES
--(1, 'Plano Mensal', 99.90, 1, 'Acesso a todos os equipamentos; Aulas coletivas; Vestiário e armários', TRUE),
--(2, 'Plano Trimestral', 269.90, 3, 'Acesso a todos os equipamentos; Aulas coletivas; Vestiário e armários; 5% de desconto', TRUE),
--(3, 'Plano Semestral', 509.90, 6, 'Acesso a todos os equipamentos; Aulas coletivas; Vestiário e armários; Avaliação física gratuita; 10% de desconto', TRUE),
--(4, 'Plano Anual', 899.90, 12, 'Acesso a todos os equipamentos; Aulas coletivas; Vestiário e armários; Avaliação física trimestral; Personal trainer 1x/mês; 20% de desconto', TRUE);

-- =============================================================================
-- FIM - Dados iniciais inseridos com sucesso!
-- =============================================================================
--
-- CREDENCIAIS DE ACESSO:
-- Email: admin@academia.com
-- Senha: admin123
--
-- IMPORTANTE: Trocar senha do admin em produção!
-- =============================================================================