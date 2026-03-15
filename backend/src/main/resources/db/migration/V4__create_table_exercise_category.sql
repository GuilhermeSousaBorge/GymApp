DROP TABLE IF EXISTS exercise_categories;

CREATE TABLE exercise_categories (
                                     id BIGSERIAL PRIMARY KEY,
                                     muscle_group VARCHAR(100) NOT NULL UNIQUE,
                                     description TEXT,
                                     active BOOLEAN NOT NULL DEFAULT TRUE,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_exercise_categories_updated_at
    BEFORE UPDATE ON exercise_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_exercise_categories_muscle_group ON exercise_categories(muscle_group);