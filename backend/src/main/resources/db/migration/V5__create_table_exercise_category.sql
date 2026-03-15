DROP TABLE IF EXISTS exercise_categories;

CREATE TABLE exercise_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    muscle_group varchar(100) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_exercise_categories_muscle_group ON exercise_categories(muscle_group);