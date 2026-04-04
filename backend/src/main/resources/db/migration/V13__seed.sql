-- =============================================================================
-- SEED.sql
-- =============================================================================
-- Ordem de inserção respeitando FKs:
-- roles → addresses → users → exercise_categories → exercises →
-- training_programs → training_sheets → training_sheet_days →
-- training_exercises → plan_benefits → subscriptions → payments
-- =============================================================================

-- =============================================================================
-- 1. ROLES
-- =============================================================================
INSERT INTO roles (name, description) VALUES
                                          ('Administrador',    'Administrador do sistema'),
                                          ('PersonalTrainer', 'Personal trainer'),
                                          ('Aluno',  'Aluno da academia')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission)
SELECT r.id, p.permission FROM roles r
           JOIN (VALUES
                     ('Administrador',    'USER_READ'),
                     ('Administrador',    'USER_WRITE'),
                     ('Administrador',    'USER_DELETE'),
                     ('Administrador',    'PLAN_READ'),
                     ('Administrador',    'PLAN_WRITE'),
                     ('Administrador',    'REPORT_READ'),
                     ('PersonalTrainer', 'USER_READ'),
                     ('PersonalTrainer', 'PROGRAM_WRITE'),
                     ('PersonalTrainer', 'PROGRAM_READ'),
                     ('Aluno',  'PROGRAM_READ'),
                     ('Aluno',  'USER_READ')
) AS p(role_name, permission) ON r.name = p.role_name
    ON CONFLICT DO NOTHING;

-- =============================================================================
-- 2. ADDRESSES
-- =============================================================================
INSERT INTO addresses (number, zip_code, district, street_name, city, state) VALUES
                                                                                 (120, '38300-000', 'Centro',            'Rua Sete de Setembro',  'Ituiutaba',      'MG'),
                                                                                 (45,  '38302-100', 'Bairro Industrial', 'Avenida 13',            'Ituiutaba',      'MG'),
                                                                                 (300, '38304-200', 'Setor Sul',         'Rua Minas Gerais',      'Ituiutaba',      'MG'),
                                                                                 (88,  '38301-050', 'Centro',            'Rua Oito',              'Ituiutaba',      'MG'),
                                                                                 (210, '38303-150', 'Residencial Leste', 'Avenida Leopoldino',    'Ituiutaba',      'MG'),
                                                                                 (15,  '01310-100', 'Bela Vista',        'Avenida Paulista',      'São Paulo',      'SP'),
                                                                                 (500, '30130-010', 'Centro',            'Rua dos Caetés',        'Belo Horizonte', 'MG'),
                                                                                 (77,  '20040-020', 'Centro',            'Rua da Assembleia',     'Rio de Janeiro', 'RJ'),
                                                                                 (33,  '38305-400', 'Novo Horizonte',    'Rua Goiás',             'Ituiutaba',      'MG'),
                                                                                 (99,  '38306-500', 'Vila Esperança',    'Travessa das Acácias',  'Ituiutaba',      'MG'),
                                                                                 (250, '38307-600', 'Parque das Flores', 'Rua das Orquídeas',     'Ituiutaba',      'MG'),
                                                                                 (18,  '38308-700', 'Setor Norte',       'Avenida Brasil',        'Ituiutaba',      'MG');

-- =============================================================================
-- 3. USERS
-- Senha de todos: "senha123" → bcrypt hash (custo 10)
-- $2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2
-- =============================================================================
INSERT INTO users (name, email, password_hash, cpf, phone, gender, birth_date, active, role_id, address_id) VALUES

                ('Admin Sistema', 'admin@fitapp.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '000.000.000-00', '(34) 99900-0000', 'MALE', '1990-01-01', TRUE,
                 (SELECT id FROM roles WHERE name = 'Administrador'),
                 (SELECT id FROM addresses WHERE zip_code = '38300-000' AND number = 120)),

                ('Carlos Mendes', 'carlos.personal@fitapp.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '111.222.333-44', '(34) 99111-1111', 'MALE', '1988-05-15', TRUE,
                 (SELECT id FROM roles WHERE name = 'PersonalTrainer'),
                 (SELECT id FROM addresses WHERE zip_code = '38302-100' AND number = 45)),

                ('Fernanda Oliveira', 'fernanda.pt@fitapp.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '222.333.444-55', '(34) 99222-2222', 'FEMALE', '1992-08-22', TRUE,
                 (SELECT id FROM roles WHERE name = 'PersonalTrainer'),
                 (SELECT id FROM addresses WHERE zip_code = '38304-200' AND number = 300)),

                ('Rafael Torres', 'rafael.pt@fitapp.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '333.444.555-66', '(11) 99333-3333', 'MALE', '1985-11-30', TRUE,
                 (SELECT id FROM roles WHERE name = 'PersonalTrainer'),
                 (SELECT id FROM addresses WHERE zip_code = '01310-100' AND number = 15)),

                ('Lucas Ferreira', 'lucas@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '444.555.666-77', '(34) 98444-4444', 'MALE', '2000-03-10', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38301-050' AND number = 88)),

                ('Mariana Costa', 'mariana@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '555.666.777-88', '(34) 98555-5555', 'FEMALE', '1999-07-18', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38303-150' AND number = 210)),

                ('Pedro Alves', 'pedro@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '666.777.888-99', '(34) 98666-6666', 'MALE', '1997-12-05', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38305-400' AND number = 33)),

                ('Juliana Ramos', 'juliana@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '777.888.999-00', '(34) 98777-7777', 'FEMALE', '2002-04-25', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38306-500' AND number = 99)),

                ('Bruno Souza', 'bruno@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '888.999.000-11', '(31) 98888-8888', 'MALE', '1995-09-14', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '30130-010' AND number = 500)),

                ('Camila Nunes', 'camila@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '999.000.111-22', '(34) 98999-9999', 'FEMALE', '2001-01-30', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38307-600' AND number = 250)),

                ('Thiago Lima', 'thiago@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '100.200.300-40', '(34) 97100-1001', 'MALE', '1998-06-08', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38308-700' AND number = 18)),

                ('Isabela Martins', 'isabela@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '200.300.400-50', '(21) 97200-2002', 'FEMALE', '2003-11-17', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '20040-020' AND number = 77)),

                ('Diego Campos', 'diego@aluno.com',
                 '$2a$10$kZhwMWpjH18r5GybcvMFi.ssdgENLytnyn4VTyuHqt5Mm7K0QLCw2',
                 '300.400.500-60', '(34) 97300-3003', 'MALE', '1993-02-20', TRUE,
                 (SELECT id FROM roles WHERE name = 'Aluno'),
                 (SELECT id FROM addresses WHERE zip_code = '38300-000' AND number = 120));

-- =============================================================================
-- 4. EXERCISE CATEGORIES
-- =============================================================================
INSERT INTO exercise_categories (muscle_group, description, active) VALUES
                                                                        ('Peitoral',          'Músculos do peitoral maior e menor',                 TRUE),
                                                                        ('Costas',            'Músculos dorsais, trapézio e romboides',             TRUE),
                                                                        ('Ombros',            'Deltoides anterior, lateral e posterior',             TRUE),
                                                                        ('Bíceps',            'Músculos da parte anterior do braço',                TRUE),
                                                                        ('Tríceps',           'Músculos da parte posterior do braço',               TRUE),
                                                                        ('Quadríceps Livre',  'Quadríceps com exercícios livres e compostos',       TRUE),
                                                                        ('Quadríceps Máquina','Quadríceps com foco em máquinas de isolamento',      TRUE),
                                                                        ('Posterior de Coxa', 'Músculos posteriores da coxa (isquiotibiais)',       TRUE),
                                                                        ('Glúteos',           'Glúteo máximo, médio e mínimo',                      TRUE),
                                                                        ('Panturrilha',       'Gastrocnêmio e sóleo',                               TRUE),
                                                                        ('Core',              'Abdômen e musculatura estabilizadora do tronco',     TRUE),
                                                                        ('Cardio',            'Exercícios cardiovasculares e aeróbicos',            TRUE)
    ON CONFLICT (muscle_group) DO NOTHING;

-- =============================================================================
-- 5. EXERCISES
-- =============================================================================
INSERT INTO exercises (name, description, category_id, equipment, active) VALUES

                                                                              ('Supino Reto',      'Exercício clássico para desenvolvimento do peitoral maior',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Peitoral'), 'Barra', TRUE),
                                                                              ('Supino Inclinado', 'Foco na parte superior do peitoral',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Peitoral'), 'Barra', TRUE),
                                                                              ('Crucifixo',        'Exercício de isolamento para o peitoral',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Peitoral'), 'Halteres', TRUE),

                                                                              ('Puxada na Frente',  'Exercício para dorsais com pegada aberta',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Costas'), 'Máquina', TRUE),
                                                                              ('Remada Curvada',    'Exercício composto para desenvolvimento das costas',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Costas'), 'Barra', TRUE),
                                                                              ('Remada Unilateral', 'Remada com foco em dorsal e estabilidade',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Costas'), 'Halter', TRUE),

                                                                              ('Desenvolvimento Militar', 'Exercício composto para deltoides',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Ombros'), 'Barra', TRUE),
                                                                              ('Elevação Lateral',  'Isolamento para deltoide lateral',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Ombros'), 'Halteres', TRUE),
                                                                              ('Elevação Frontal',  'Foco no deltoide anterior',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Ombros'), 'Halteres', TRUE),

                                                                              ('Rosca Direta',    'Exercício clássico para bíceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Bíceps'), 'Barra', TRUE),
                                                                              ('Rosca Alternada', 'Rosca unilateral para bíceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Bíceps'), 'Halteres', TRUE),
                                                                              ('Rosca Scott',     'Isolamento máximo do bíceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Bíceps'), 'Banco Scott', TRUE),

                                                                              ('Tríceps Corda',  'Extensão de tríceps na polia com corda',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Tríceps'), 'Polia', TRUE),
                                                                              ('Tríceps Testa',  'Exercício focado na cabeça longa do tríceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Tríceps'), 'Barra W', TRUE),
                                                                              ('Mergulho',       'Exercício composto para tríceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Tríceps'), 'Peso corporal', TRUE),

                                                                              ('Agachamento Livre', 'Exercício composto para membros inferiores',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Quadríceps Livre'), 'Barra', TRUE),
                                                                              ('Leg Press',         'Exercício para quadríceps e glúteos',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Quadríceps Livre'), 'Máquina', TRUE),

                                                                              ('Cadeira Extensora', 'Isolamento do quadríceps',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Quadríceps Máquina'), 'Máquina', TRUE),
                                                                              ('Hack Machine',      'Foco em quadríceps com suporte lombar',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Quadríceps Máquina'), 'Máquina', TRUE),

                                                                              ('Mesa Flexora', 'Isolamento dos posteriores de coxa',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Posterior de Coxa'), 'Máquina', TRUE),
                                                                              ('Stiff',        'Exercício para posterior e glúteos',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Posterior de Coxa'), 'Barra', TRUE),

                                                                              ('Elevação Pélvica', 'Exercício principal para glúteos',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Glúteos'), 'Barra', TRUE),
                                                                              ('Glúteo no Cabo',   'Extensão de quadril focada em glúteo',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Glúteos'), 'Polia', TRUE),

                                                                              ('Elevação de Panturrilha em Pé',   'Trabalho do gastrocnêmio',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Panturrilha'), 'Máquina', TRUE),
                                                                              ('Elevação de Panturrilha Sentado', 'Foco no sóleo',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Panturrilha'), 'Máquina', TRUE),

                                                                              ('Abdominal Tradicional', 'Flexão de tronco para abdômen',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Core'), 'Peso corporal', TRUE),
                                                                              ('Prancha',               'Exercício isométrico para core',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Core'), 'Peso corporal', TRUE),

                                                                              ('Esteira',               'Corrida ou caminhada em esteira',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Cardio'), 'Esteira', TRUE),
                                                                              ('Bicicleta Ergométrica', 'Exercício cardiovascular em bicicleta',
                                                                               (SELECT id FROM exercise_categories WHERE muscle_group = 'Cardio'), 'Bicicleta', TRUE)

    ON CONFLICT (name) DO NOTHING;

-- =============================================================================
-- 6. PLAN BENEFITS
-- (plans já inseridos pela V10)
-- =============================================================================
INSERT INTO plan_benefits (plan_id, benefits)
SELECT p.id, b.benefit FROM plans p
                                JOIN (VALUES
                                          ('Free',    'Até 5 alunos'),
                                          ('Free',    '1 programa por aluno'),
                                          ('Free',    'Acesso básico à plataforma'),
                                          ('Basic',   'Até 20 alunos'),
                                          ('Basic',   '3 programas por aluno'),
                                          ('Basic',   'Relatórios básicos'),
                                          ('Basic',   'Suporte por email'),
                                          ('Premium', 'Alunos ilimitados'),
                                          ('Premium', 'Programas ilimitados'),
                                          ('Premium', 'Relatórios avançados'),
                                          ('Premium', 'Suporte prioritário'),
                                          ('Premium', 'App personalizado')
) AS b(plan_name, benefit) ON p.name = b.plan_name
    ON CONFLICT DO NOTHING;

-- =============================================================================
-- 7. TRAINING PROGRAMS
-- =============================================================================
INSERT INTO training_programs (name, description, user_id, trainer_id, active) VALUES

                                                                                   ('Hipertrofia ABC - Lucas',
                                                                                    'Programa de hipertrofia dividido em 3 dias para ganho de massa muscular.',
                                                                                    (SELECT id FROM users WHERE email = 'lucas@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'carlos.personal@fitapp.com'), TRUE),

                                                                                   ('Definição Full Body - Mariana',
                                                                                    'Programa de definição com treinos full body 3x por semana.',
                                                                                    (SELECT id FROM users WHERE email = 'mariana@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'fernanda.pt@fitapp.com'), TRUE),

                                                                                   ('Força e Hipertrofia - Pedro',
                                                                                    'Treino focado em ganho de força com volume moderado.',
                                                                                    (SELECT id FROM users WHERE email = 'pedro@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'carlos.personal@fitapp.com'), TRUE),

                                                                                   ('Glúteos e Pernas - Juliana',
                                                                                    'Programa especializado em membros inferiores e glúteos.',
                                                                                    (SELECT id FROM users WHERE email = 'juliana@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'fernanda.pt@fitapp.com'), TRUE),

                                                                                   ('Emagrecimento - Bruno',
                                                                                    'Programa de emagrecimento com foco em cardio e treino funcional.',
                                                                                    (SELECT id FROM users WHERE email = 'bruno@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'rafael.pt@fitapp.com'), TRUE),

                                                                                   ('Iniciante Total - Camila',
                                                                                    'Programa para iniciantes com foco em aprender os movimentos.',
                                                                                    (SELECT id FROM users WHERE email = 'camila@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'fernanda.pt@fitapp.com'), TRUE),

                                                                                   ('Alta Performance - Thiago',
                                                                                    'Programa avançado para atleta recreacional.',
                                                                                    (SELECT id FROM users WHERE email = 'thiago@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'carlos.personal@fitapp.com'), TRUE),

                                                                                   ('Condicionamento - Isabela',
                                                                                    'Programa misto de condicionamento e hipertrofia leve.',
                                                                                    (SELECT id FROM users WHERE email = 'isabela@aluno.com'),
                                                                                    (SELECT id FROM users WHERE email = 'rafael.pt@fitapp.com'), TRUE),

                                                                                   ('Autogestão - Diego',
                                                                                    'Programa montado pelo próprio aluno para manutenção.',
                                                                                    (SELECT id FROM users WHERE email = 'diego@aluno.com'),
                                                                                    NULL, TRUE);

-- =============================================================================
-- 8. TRAINING SHEETS
-- =============================================================================
INSERT INTO training_sheets (name, description, active, rest_time_seconds, training_program_id, order_in_program) VALUES

                                                                                                                      ('Treino A - Peito e Tríceps', 'Foco em peitoral com auxiliares de tríceps.', TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Hipertrofia ABC - Lucas'), 1),
                                                                                                                      ('Treino B - Costas e Bíceps', 'Foco em dorsal com auxiliares de bíceps.',    TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Hipertrofia ABC - Lucas'), 2),
                                                                                                                      ('Treino C - Pernas e Ombros', 'Membros inferiores e deltoides.',              TRUE, 90,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Hipertrofia ABC - Lucas'), 3),

                                                                                                                      ('Full Body A', 'Treino completo ênfase em empurrar.', TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Definição Full Body - Mariana'), 1),
                                                                                                                      ('Full Body B', 'Treino completo ênfase em puxar.',   TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Definição Full Body - Mariana'), 2),
                                                                                                                      ('Full Body C', 'Treino completo ênfase em pernas.',  TRUE, 75,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Definição Full Body - Mariana'), 3),

                                                                                                                      ('Upper - Força', 'Treino superior com foco em força.', TRUE, 120,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Força e Hipertrofia - Pedro'), 1),
                                                                                                                      ('Lower - Força', 'Treino inferior com foco em força.', TRUE, 120,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Força e Hipertrofia - Pedro'), 2),

                                                                                                                      ('Glúteos e Posterior', 'Foco em glúteos e posterior de coxa.',   TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Glúteos e Pernas - Juliana'), 1),
                                                                                                                      ('Pernas Completo',     'Quadríceps, panturrilha e mobilidade.',   TRUE, 75,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Glúteos e Pernas - Juliana'), 2),

                                                                                                                      ('Cardio + Funcional',    'Cardio intervalado e exercícios funcionais.', TRUE, 45,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Emagrecimento - Bruno'), 1),
                                                                                                                      ('Musculação Metabólica', 'Circuito de musculação com pouco descanso.',  TRUE, 45,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Emagrecimento - Bruno'), 2),

                                                                                                                      ('Iniciante A', 'Movimentos básicos de membros superiores.', TRUE, 90,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Iniciante Total - Camila'), 1),
                                                                                                                      ('Iniciante B', 'Movimentos básicos de membros inferiores.', TRUE, 90,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Iniciante Total - Camila'), 2),

                                                                                                                      ('Push - Alta Performance', 'Empurrar: peito, ombro, tríceps.',     TRUE, 75,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Alta Performance - Thiago'), 1),
                                                                                                                      ('Pull - Alta Performance', 'Puxar: costas e bíceps.',               TRUE, 75,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Alta Performance - Thiago'), 2),
                                                                                                                      ('Legs - Alta Performance', 'Pernas completo com alta intensidade.', TRUE, 90,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Alta Performance - Thiago'), 3),

                                                                                                                      ('Condicionamento Superior', 'Superior + cardio leve.', TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Condicionamento - Isabela'), 1),
                                                                                                                      ('Condicionamento Inferior', 'Inferior + core.',        TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Condicionamento - Isabela'), 2),

                                                                                                                      ('Manutenção A', 'Treino superior de manutenção.', TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Autogestão - Diego'), 1),
                                                                                                                      ('Manutenção B', 'Treino inferior de manutenção.', TRUE, 60,
                                                                                                                       (SELECT id FROM training_programs WHERE name = 'Autogestão - Diego'), 2);

-- =============================================================================
-- 9. TRAINING SHEET DAYS
-- =============================================================================
INSERT INTO training_sheet_days (training_sheet_id, day_of_week)
SELECT ts.id, d.day_of_week FROM training_sheets ts
                                     JOIN (VALUES
                                               ('Treino A - Peito e Tríceps', 'MONDAY'),
                                               ('Treino B - Costas e Bíceps', 'WEDNESDAY'),
                                               ('Treino C - Pernas e Ombros', 'FRIDAY'),
                                               ('Full Body A',                'MONDAY'),
                                               ('Full Body B',                'WEDNESDAY'),
                                               ('Full Body C',                'FRIDAY'),
                                               ('Upper - Força',              'MONDAY'),
                                               ('Upper - Força',              'THURSDAY'),
                                               ('Lower - Força',              'TUESDAY'),
                                               ('Lower - Força',              'FRIDAY'),
                                               ('Glúteos e Posterior',        'TUESDAY'),
                                               ('Pernas Completo',            'FRIDAY'),
                                               ('Cardio + Funcional',         'MONDAY'),
                                               ('Cardio + Funcional',         'WEDNESDAY'),
                                               ('Cardio + Funcional',         'FRIDAY'),
                                               ('Musculação Metabólica',      'TUESDAY'),
                                               ('Musculação Metabólica',      'THURSDAY'),
                                               ('Iniciante A',                'MONDAY'),
                                               ('Iniciante B',                'THURSDAY'),
                                               ('Push - Alta Performance',    'MONDAY'),
                                               ('Pull - Alta Performance',    'TUESDAY'),
                                               ('Legs - Alta Performance',    'THURSDAY'),
                                               ('Condicionamento Superior',   'TUESDAY'),
                                               ('Condicionamento Inferior',   'FRIDAY'),
                                               ('Manutenção A',               'MONDAY'),
                                               ('Manutenção B',               'THURSDAY')
) AS d(sheet_name, day_of_week) ON ts.name = d.sheet_name
    ON CONFLICT DO NOTHING;

-- =============================================================================
-- 10. TRAINING EXERCISES
-- =============================================================================
INSERT INTO training_exercises (training_sheet_id, exercise_id, sets, reps, order_in_sheet, rest_time_in_seconds, technique_notes) VALUES

-- Treino A - Peito e Tríceps
((SELECT id FROM training_sheets WHERE name = 'Treino A - Peito e Tríceps'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              4, '8-12',  1, 90,  'Pegada na largura dos ombros'),
((SELECT id FROM training_sheets WHERE name = 'Treino A - Peito e Tríceps'),
 (SELECT id FROM exercises WHERE name = 'Supino Inclinado'),         3, '10-12', 2, 75,  'Cotovelo a 45 graus'),
((SELECT id FROM training_sheets WHERE name = 'Treino A - Peito e Tríceps'),
 (SELECT id FROM exercises WHERE name = 'Crucifixo'),                3, '12-15', 3, 60,  'Leve flexão nos cotovelos'),
((SELECT id FROM training_sheets WHERE name = 'Treino A - Peito e Tríceps'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Corda'),            3, '12-15', 4, 60,  'Corda separada na descida'),
((SELECT id FROM training_sheets WHERE name = 'Treino A - Peito e Tríceps'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Testa'),            3, '10-12', 5, 60,  'Barra W, não abrir muito'),

-- Treino B - Costas e Bíceps
((SELECT id FROM training_sheets WHERE name = 'Treino B - Costas e Bíceps'),
 (SELECT id FROM exercises WHERE name = 'Puxada na Frente'),         4, '10-12', 1, 75,  'Pegada pronada, aberta'),
((SELECT id FROM training_sheets WHERE name = 'Treino B - Costas e Bíceps'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           4, '8-12',  2, 90,  'Coluna neutra, cotovelo alto'),
((SELECT id FROM training_sheets WHERE name = 'Treino B - Costas e Bíceps'),
 (SELECT id FROM exercises WHERE name = 'Remada Unilateral'),        3, '10-12', 3, 60,  'Apoio no joelho, full ROM'),
((SELECT id FROM training_sheets WHERE name = 'Treino B - Costas e Bíceps'),
 (SELECT id FROM exercises WHERE name = 'Rosca Direta'),             3, '10-12', 4, 60,  'Sem balanço de tronco'),
((SELECT id FROM training_sheets WHERE name = 'Treino B - Costas e Bíceps'),
 (SELECT id FROM exercises WHERE name = 'Rosca Alternada'),          3, '12-15', 5, 45,  'Supinar no topo'),

-- Treino C - Pernas e Ombros
((SELECT id FROM training_sheets WHERE name = 'Treino C - Pernas e Ombros'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        4, '8-10',  1, 120, 'Joelhos alinhados com os pés'),
((SELECT id FROM training_sheets WHERE name = 'Treino C - Pernas e Ombros'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                3, '12-15', 2, 90,  'Pés na largura dos ombros'),
((SELECT id FROM training_sheets WHERE name = 'Treino C - Pernas e Ombros'),
 (SELECT id FROM exercises WHERE name = 'Cadeira Extensora'),        3, '15',    3, 60,  'Contração no topo'),
((SELECT id FROM training_sheets WHERE name = 'Treino C - Pernas e Ombros'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  3, '10-12', 4, 75,  'Cotovelos na frente'),
((SELECT id FROM training_sheets WHERE name = 'Treino C - Pernas e Ombros'),
 (SELECT id FROM exercises WHERE name = 'Elevação Lateral'),         3, '15',    5, 45,  'Cotovelos levemente flexionados'),

-- Full Body A
((SELECT id FROM training_sheets WHERE name = 'Full Body A'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              3, '10', 1, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body A'),
 (SELECT id FROM exercises WHERE name = 'Puxada na Frente'),         3, '10', 2, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body A'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  3, '12', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body A'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Corda'),            2, '15', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body A'),
 (SELECT id FROM exercises WHERE name = 'Rosca Direta'),             2, '15', 5, 45, NULL),

-- Full Body B
((SELECT id FROM training_sheets WHERE name = 'Full Body B'),
 (SELECT id FROM exercises WHERE name = 'Supino Inclinado'),         3, '10', 1, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body B'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           3, '10', 2, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body B'),
 (SELECT id FROM exercises WHERE name = 'Elevação Lateral'),         3, '12', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body B'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Testa'),            2, '15', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body B'),
 (SELECT id FROM exercises WHERE name = 'Rosca Alternada'),          2, '15', 5, 45, NULL),

-- Full Body C
((SELECT id FROM training_sheets WHERE name = 'Full Body C'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        4, '12', 1, 90, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body C'),
 (SELECT id FROM exercises WHERE name = 'Mesa Flexora'),             3, '15', 2, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body C'),
 (SELECT id FROM exercises WHERE name = 'Elevação Pélvica'),         3, '15', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body C'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 3, '20', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Full Body C'),
 (SELECT id FROM exercises WHERE name = 'Abdominal Tradicional'),    3, '20', 5, 30, NULL),

-- Upper - Força
((SELECT id FROM training_sheets WHERE name = 'Upper - Força'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              5, '5', 1, 180, 'Progressão de carga semanal'),
((SELECT id FROM training_sheets WHERE name = 'Upper - Força'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           5, '5', 2, 180, 'Pegada pronada, sem momentum'),
((SELECT id FROM training_sheets WHERE name = 'Upper - Força'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  4, '6', 3, 150, 'Barra livre estrito'),
((SELECT id FROM training_sheets WHERE name = 'Upper - Força'),
 (SELECT id FROM exercises WHERE name = 'Rosca Direta'),             3, '8', 4, 90,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Upper - Força'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Testa'),            3, '8', 5, 90,  NULL),

-- Lower - Força
((SELECT id FROM training_sheets WHERE name = 'Lower - Força'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        5, '5',    1, 180, 'Agachamento profundo'),
((SELECT id FROM training_sheets WHERE name = 'Lower - Força'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                4, '8',    2, 120, 'Plataforma alta'),
((SELECT id FROM training_sheets WHERE name = 'Lower - Força'),
 (SELECT id FROM exercises WHERE name = 'Stiff'),                    3, '8-10', 3, 90,  'Peso moderado, hip hinge'),
((SELECT id FROM training_sheets WHERE name = 'Lower - Força'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 4, '12', 4, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Lower - Força'),
 (SELECT id FROM exercises WHERE name = 'Prancha'),                  3, '60s',  5, 60,  'Isométrico'),

-- Glúteos e Posterior
((SELECT id FROM training_sheets WHERE name = 'Glúteos e Posterior'),
 (SELECT id FROM exercises WHERE name = 'Elevação Pélvica'),         4, '15', 1, 60, 'Barra sobre quadril, hip thrust'),
((SELECT id FROM training_sheets WHERE name = 'Glúteos e Posterior'),
 (SELECT id FROM exercises WHERE name = 'Glúteo no Cabo'),           3, '15', 2, 60, 'Extensão total do quadril'),
((SELECT id FROM training_sheets WHERE name = 'Glúteos e Posterior'),
 (SELECT id FROM exercises WHERE name = 'Stiff'),                    3, '12', 3, 75, 'Joelhos levemente flexionados'),
((SELECT id FROM training_sheets WHERE name = 'Glúteos e Posterior'),
 (SELECT id FROM exercises WHERE name = 'Mesa Flexora'),             3, '15', 4, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Glúteos e Posterior'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha Sentado'), 3, '20', 5, 45, NULL),

-- Pernas Completo
((SELECT id FROM training_sheets WHERE name = 'Pernas Completo'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                4, '12-15', 1, 90, 'Pés na largura dos ombros'),
((SELECT id FROM training_sheets WHERE name = 'Pernas Completo'),
 (SELECT id FROM exercises WHERE name = 'Cadeira Extensora'),        3, '15',    2, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pernas Completo'),
 (SELECT id FROM exercises WHERE name = 'Hack Machine'),             3, '12',    3, 75, 'Suporte lombar encostado'),
((SELECT id FROM training_sheets WHERE name = 'Pernas Completo'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 4, '20', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pernas Completo'),
 (SELECT id FROM exercises WHERE name = 'Prancha'),                  3, '45s',   5, 45, NULL),

-- Cardio + Funcional
((SELECT id FROM training_sheets WHERE name = 'Cardio + Funcional'),
 (SELECT id FROM exercises WHERE name = 'Esteira'),                  1, '30min', 1, 0,  'Velocidade 8-10 km/h'),
((SELECT id FROM training_sheets WHERE name = 'Cardio + Funcional'),
 (SELECT id FROM exercises WHERE name = 'Abdominal Tradicional'),    3, '20',    2, 30, NULL),
((SELECT id FROM training_sheets WHERE name = 'Cardio + Funcional'),
 (SELECT id FROM exercises WHERE name = 'Prancha'),                  3, '45s',   3, 30, NULL),
((SELECT id FROM training_sheets WHERE name = 'Cardio + Funcional'),
 (SELECT id FROM exercises WHERE name = 'Bicicleta Ergométrica'),    1, '15min', 4, 0,  'Nível 8 de resistência'),

-- Musculação Metabólica
((SELECT id FROM training_sheets WHERE name = 'Musculação Metabólica'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        4, '15', 1, 45, 'Peso moderado, ritmo alto'),
((SELECT id FROM training_sheets WHERE name = 'Musculação Metabólica'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              4, '15', 2, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Musculação Metabólica'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           4, '15', 3, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Musculação Metabólica'),
 (SELECT id FROM exercises WHERE name = 'Elevação Pélvica'),         3, '20', 4, 30, NULL),
((SELECT id FROM training_sheets WHERE name = 'Musculação Metabólica'),
 (SELECT id FROM exercises WHERE name = 'Abdominal Tradicional'),    3, '20', 5, 30, NULL),

-- Iniciante A
((SELECT id FROM training_sheets WHERE name = 'Iniciante A'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              3, '10', 1, 90, 'Carga leve, foco na técnica'),
((SELECT id FROM training_sheets WHERE name = 'Iniciante A'),
 (SELECT id FROM exercises WHERE name = 'Puxada na Frente'),         3, '10', 2, 90, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante A'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  3, '12', 3, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante A'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Corda'),            2, '15', 4, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante A'),
 (SELECT id FROM exercises WHERE name = 'Rosca Direta'),             2, '15', 5, 60, NULL),

-- Iniciante B
((SELECT id FROM training_sheets WHERE name = 'Iniciante B'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                3, '12', 1, 90, 'Pés paralelos'),
((SELECT id FROM training_sheets WHERE name = 'Iniciante B'),
 (SELECT id FROM exercises WHERE name = 'Cadeira Extensora'),        3, '15', 2, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante B'),
 (SELECT id FROM exercises WHERE name = 'Mesa Flexora'),             3, '15', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante B'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 3, '20', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Iniciante B'),
 (SELECT id FROM exercises WHERE name = 'Abdominal Tradicional'),    3, '15', 5, 45, NULL),

-- Push - Alta Performance
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              5, '6-8',  1, 120, 'RPE 8'),
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Supino Inclinado'),         4, '8-10', 2, 90,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Crucifixo'),                3, '12',   3, 60,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  4, '8',    4, 90,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Elevação Lateral'),         3, '15',   5, 45,  'Drop set na última'),
((SELECT id FROM training_sheets WHERE name = 'Push - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Corda'),            3, '12',   6, 60,  NULL),

-- Pull - Alta Performance
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Puxada na Frente'),         5, '8',  1, 90, 'Pegada neutra alternada'),
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           4, '8',  2, 90, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Remada Unilateral'),        3, '10', 3, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Elevação Frontal'),         3, '15', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Rosca Direta'),             4, '10', 5, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Pull - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Rosca Scott'),              3, '12', 6, 60, 'Banco inclinado 45°'),

-- Legs - Alta Performance
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        5, '8',  1, 150, 'Cinto de levantamento'),
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Hack Machine'),             4, '10', 2, 90,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Cadeira Extensora'),        3, '15', 3, 60,  'Pré-exaustão'),
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Stiff'),                    4, '10', 4, 90,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Mesa Flexora'),             3, '15', 5, 60,  NULL),
((SELECT id FROM training_sheets WHERE name = 'Legs - Alta Performance'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 5, '20', 6, 45, 'Explosivo na subida'),

-- Condicionamento Superior
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Superior'),
 (SELECT id FROM exercises WHERE name = 'Supino Inclinado'),         3, '12',    1, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Superior'),
 (SELECT id FROM exercises WHERE name = 'Remada Unilateral'),        3, '12',    2, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Superior'),
 (SELECT id FROM exercises WHERE name = 'Elevação Lateral'),         3, '15',    3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Superior'),
 (SELECT id FROM exercises WHERE name = 'Mergulho'),                 3, '12',    4, 60, 'Peso corporal'),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Superior'),
 (SELECT id FROM exercises WHERE name = 'Bicicleta Ergométrica'),    1, '20min', 5, 0,  'Nível 6, cadência constante'),

-- Condicionamento Inferior
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Inferior'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                3, '15',  1, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Inferior'),
 (SELECT id FROM exercises WHERE name = 'Elevação Pélvica'),         3, '15',  2, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Inferior'),
 (SELECT id FROM exercises WHERE name = 'Mesa Flexora'),             3, '15',  3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Inferior'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha Sentado'), 3, '20', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Condicionamento Inferior'),
 (SELECT id FROM exercises WHERE name = 'Prancha'),                  3, '40s', 5, 45, NULL),

-- Manutenção A
((SELECT id FROM training_sheets WHERE name = 'Manutenção A'),
 (SELECT id FROM exercises WHERE name = 'Supino Reto'),              3, '10', 1, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção A'),
 (SELECT id FROM exercises WHERE name = 'Remada Curvada'),           3, '10', 2, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção A'),
 (SELECT id FROM exercises WHERE name = 'Desenvolvimento Militar'),  3, '12', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção A'),
 (SELECT id FROM exercises WHERE name = 'Tríceps Corda'),            2, '15', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção A'),
 (SELECT id FROM exercises WHERE name = 'Rosca Alternada'),          2, '15', 5, 45, NULL),

-- Manutenção B
((SELECT id FROM training_sheets WHERE name = 'Manutenção B'),
 (SELECT id FROM exercises WHERE name = 'Agachamento Livre'),        3, '12', 1, 90, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção B'),
 (SELECT id FROM exercises WHERE name = 'Leg Press'),                3, '15', 2, 75, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção B'),
 (SELECT id FROM exercises WHERE name = 'Elevação Pélvica'),         3, '15', 3, 60, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção B'),
 (SELECT id FROM exercises WHERE name = 'Elevação de Panturrilha em Pé'), 3, '20', 4, 45, NULL),
((SELECT id FROM training_sheets WHERE name = 'Manutenção B'),
 (SELECT id FROM exercises WHERE name = 'Abdominal Tradicional'),    3, '20', 5, 30, NULL);

-- =============================================================================
-- 11. SUBSCRIPTIONS
-- =============================================================================
INSERT INTO subscriptions (plan_id, user_id, start_date, end_date, status, cancelled_at, auto_renew, plan_price_at_start) VALUES

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Premium'),
                                                                                                                               (SELECT id FROM users WHERE email = 'carlos.personal@fitapp.com'),
                                                                                                                               '2025-01-01', '2026-01-01', 'ACTIVE', NULL, TRUE, 149.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Basic'),
                                                                                                                               (SELECT id FROM users WHERE email = 'fernanda.pt@fitapp.com'),
                                                                                                                               '2025-03-01', '2026-03-01', 'ACTIVE', NULL, TRUE, 49.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Free'),
                                                                                                                               (SELECT id FROM users WHERE email = 'rafael.pt@fitapp.com'),
                                                                                                                               '2025-06-01', NULL, 'ACTIVE', NULL, FALSE, 0.00),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Basic'),
                                                                                                                               (SELECT id FROM users WHERE email = 'lucas@aluno.com'),
                                                                                                                               '2025-02-01', '2026-02-01', 'ACTIVE', NULL, TRUE, 49.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Free'),
                                                                                                                               (SELECT id FROM users WHERE email = 'mariana@aluno.com'),
                                                                                                                               '2025-04-01', NULL, 'ACTIVE', NULL, FALSE, 0.00),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Premium'),
                                                                                                                               (SELECT id FROM users WHERE email = 'pedro@aluno.com'),
                                                                                                                               '2024-11-01', '2025-11-01', 'EXPIRED', NULL, FALSE, 149.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Basic'),
                                                                                                                               (SELECT id FROM users WHERE email = 'juliana@aluno.com'),
                                                                                                                               '2025-05-01', '2026-05-01', 'ACTIVE', NULL, TRUE, 49.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Free'),
                                                                                                                               (SELECT id FROM users WHERE email = 'bruno@aluno.com'),
                                                                                                                               '2025-07-01', '2025-10-01', 'CANCELLED', '2025-09-15 10:30:00', FALSE, 0.00),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Basic'),
                                                                                                                               (SELECT id FROM users WHERE email = 'camila@aluno.com'),
                                                                                                                               '2025-08-01', '2026-08-01', 'ACTIVE', NULL, TRUE, 49.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Premium'),
                                                                                                                               (SELECT id FROM users WHERE email = 'thiago@aluno.com'),
                                                                                                                               '2025-01-15', '2026-01-15', 'ACTIVE', NULL, TRUE, 149.90),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Free'),
                                                                                                                               (SELECT id FROM users WHERE email = 'isabela@aluno.com'),
                                                                                                                               '2025-09-01', NULL, 'PAST_DUE', NULL, TRUE, 0.00),

                                                                                                                              ((SELECT id FROM plans WHERE name = 'Basic'),
                                                                                                                               (SELECT id FROM users WHERE email = 'diego@aluno.com'),
                                                                                                                               '2025-10-01', '2026-10-01', 'ACTIVE', NULL, TRUE, 49.90);

-- =============================================================================
-- 12. PAYMENTS
-- =============================================================================
INSERT INTO payments (subscription_id, status, amount, due_date, payment_date, payment_method) VALUES

-- Carlos - Premium
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-01-01', '2025-01-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-02-01', '2025-02-03', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-03-01', '2025-03-02', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-04-01', '2025-04-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-05-01', '2025-05-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-06-01', '2025-06-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-07-01', '2025-07-03', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-08-01', '2025-08-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-09-01', '2025-09-02', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-10-01', '2025-10-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-11-01', '2025-11-04', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'carlos.personal@fitapp.com'),
 'PAID', 149.90, '2025-12-01', '2025-12-01', 'CREDIT_CARD'),

-- Fernanda - Basic
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-03-01', '2025-03-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-04-01', '2025-04-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-05-01', '2025-05-05', 'BOLETO'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-06-01', '2025-06-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-07-01', '2025-07-03', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-08-01', '2025-08-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-09-01', '2025-09-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-10-01', '2025-10-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-11-01', '2025-11-01', 'BOLETO'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'fernanda.pt@fitapp.com'),
 'PAID', 49.90, '2025-12-01', '2025-12-03', 'PIX'),

-- Lucas - Basic
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-02-01', '2025-02-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-03-01', '2025-03-03', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-04-01', '2025-04-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-05-01', '2025-05-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-06-01', '2025-06-05', 'BOLETO'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-07-01', '2025-07-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-08-01', '2025-08-04', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-09-01', '2025-09-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-10-01', '2025-10-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-11-01', '2025-11-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'lucas@aluno.com'),
 'PAID', 49.90, '2025-12-01', '2025-12-03', 'PIX'),

-- Pedro - Premium EXPIRED
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'PAID', 149.90, '2024-11-01', '2024-11-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'PAID', 149.90, '2024-12-01', '2024-12-02', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'PAID', 149.90, '2025-01-01', '2025-01-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'PAID', 149.90, '2025-02-01', '2025-02-03', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'PAID', 149.90, '2025-03-01', '2025-03-01', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'FAILED', 149.90, '2025-04-01', NULL, 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'pedro@aluno.com'),
 'REFUNDED', 149.90, '2025-04-15', '2025-04-10', 'CREDIT_CARD'),

-- Juliana - Basic
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-05-01', '2025-05-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-06-01', '2025-06-03', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-07-01', '2025-07-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-08-01', '2025-08-02', 'BOLETO'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-09-01', '2025-09-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-10-01', '2025-10-04', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-11-01', '2025-11-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'juliana@aluno.com'),
 'PAID', 49.90, '2025-12-01', '2025-12-02', 'PIX'),

-- Bruno - Free CANCELLED
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'bruno@aluno.com'),
 'PAID', 0.00, '2025-07-01', '2025-07-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'bruno@aluno.com'),
 'PAID', 0.00, '2025-08-01', '2025-08-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'bruno@aluno.com'),
 'CANCELLED', 0.00, '2025-09-01', NULL, 'PIX'),

-- Camila - Basic
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'camila@aluno.com'),
 'PAID', 49.90, '2025-08-01', '2025-08-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'camila@aluno.com'),
 'PAID', 49.90, '2025-09-01', '2025-09-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'camila@aluno.com'),
 'PAID', 49.90, '2025-10-01', '2025-10-03', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'camila@aluno.com'),
 'PAID', 49.90, '2025-11-01', '2025-11-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'camila@aluno.com'),
 'PAID', 49.90, '2025-12-01', '2025-12-05', 'PIX'),

-- Thiago - Premium
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-01-15', '2025-01-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-02-15', '2025-02-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-03-15', '2025-03-17', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-04-15', '2025-04-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-05-15', '2025-05-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-06-15', '2025-06-16', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-07-15', '2025-07-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-08-15', '2025-08-15', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-09-15', '2025-09-15', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-10-15', '2025-10-17', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-11-15', '2025-11-15', 'CREDIT_CARD'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'thiago@aluno.com'),
 'PAID', 149.90, '2025-12-15', '2025-12-15', 'CREDIT_CARD'),

-- Isabela - Free PAST_DUE
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'isabela@aluno.com'),
 'PAID', 0.00, '2025-09-01', '2025-09-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'isabela@aluno.com'),
 'PAID', 0.00, '2025-10-01', '2025-10-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'isabela@aluno.com'),
 'PENDING', 0.00, '2025-11-01', NULL, 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'isabela@aluno.com'),
 'PENDING', 0.00, '2025-12-01', NULL, 'PIX'),

-- Diego - Basic
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'diego@aluno.com'),
 'PAID', 49.90, '2025-10-01', '2025-10-01', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'diego@aluno.com'),
 'PAID', 49.90, '2025-11-01', '2025-11-02', 'PIX'),
((SELECT s.id FROM subscriptions s JOIN users u ON s.user_id = u.id WHERE u.email = 'diego@aluno.com'),
 'PAID', 49.90, '2025-12-01', '2025-12-01', 'BOLETO');