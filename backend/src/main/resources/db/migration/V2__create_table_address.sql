-- =============================================================================
-- V2__create_addresses_table.sql
-- =============================================================================
-- Cria tabela de endereços
-- =============================================================================

CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           number INT,
                           zip_code VARCHAR(10),
                           district VARCHAR(100),
                           street_name VARCHAR(150),
                           city VARCHAR(100),
                           state VARCHAR(2)
);

-- Índices para buscas comuns
CREATE INDEX idx_addresses_city ON addresses(city);
CREATE INDEX idx_addresses_state ON addresses(state);
CREATE INDEX idx_addresses_zip_code ON addresses(zip_code);