CREATE TABLE IF NOT EXISTS plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(10,2) NOT NULL,
    max_students INT NOT NULL DEFAULT 0,
    max_programs INT NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS plan_benefits (
    plan_id BIGINT NOT NULL,
    benefits VARCHAR(255) NOT NULL,
    PRIMARY KEY (plan_id, benefits),
    CONSTRAINT fk_plan_benefits_plan FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_plans_name ON plans(name);
CREATE INDEX IF NOT EXISTS idx_plans_active ON plans(active);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_trigger
        WHERE tgname = 'trg_plans_updated_at'
    ) THEN
        CREATE TRIGGER trg_plans_updated_at
            BEFORE UPDATE ON plans
            FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
    END IF;
END $$;

INSERT INTO plans (name, description, price, max_students, max_programs, active)
SELECT 'Free', 'Plano gratuito - ate 5 alunos e 1 programa por aluno.', 0.00, 5, 1, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'Free');

INSERT INTO plans (name, description, price, max_students, max_programs, active)
SELECT 'Basic', 'Plano basico - ate 20 alunos e 3 programas por aluno.', 49.90, 20, 3, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'Basic');

INSERT INTO plans (name, description, price, max_students, max_programs, active)
SELECT 'Premium', 'Plano premium - alunos ilimitados e programas ilimitados.', 149.90, 999, 99, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'Premium');

