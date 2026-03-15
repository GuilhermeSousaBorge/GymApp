-- =============================================================================
-- V3__create_users_table.sql
-- =============================================================================
-- Cria tabela de usuários do sistema
-- =============================================================================

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(150) NOT NULL,
                       email VARCHAR(150) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       cpf VARCHAR(14) UNIQUE,
                       phone VARCHAR(20),
                       gender VARCHAR(10) NOT NULL,
                       birth_date DATE,
                       active BOOLEAN NOT NULL DEFAULT TRUE,

                       role_id BIGINT NOT NULL,
                       address_id BIGINT,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
                       CONSTRAINT fk_users_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL,
                       CONSTRAINT chk_users_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

-- Trigger para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Índices para performance e buscas
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cpf ON users(cpf);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_created_at ON users(created_at);