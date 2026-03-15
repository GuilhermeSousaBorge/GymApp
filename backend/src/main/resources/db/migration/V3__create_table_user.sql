-- =============================================================================
-- V3__create_users_table.sql
-- =============================================================================
-- Cria tabela de usuários do sistema
-- =============================================================================

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    phone VARCHAR(20),
    gender VARCHAR(10) NOT NULL,
    birth_date TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    role_id BIGINT NOT NULL,
    address_id BIGINT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_users_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL,
    CONSTRAINT chk_users_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER'))
);

-- Índices para performance e buscas
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cpf ON users(cpf);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_users_created_at ON users(created_at);