CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description TEXT
);

-- Tabela de permissões (muitos-para-muitos com roles)
CREATE TABLE role_permissions (
                                  role_id BIGINT NOT NULL,
                                  permission VARCHAR(50) NOT NULL,
                                  PRIMARY KEY (role_id, permission),
                                  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);