CREATE TABLE exercises (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL UNIQUE,
                           description TEXT,
                           category_id BIGINT,
                           equipment VARCHAR(100),
                           active BOOLEAN DEFAULT TRUE,
                           video_url VARCHAR(255),
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_exercise_category FOREIGN KEY (category_id) REFERENCES exercise_categories(id)
);

CREATE TRIGGER trg_exercises_updated_at
    BEFORE UPDATE ON exercises
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_exercises_name ON exercises(name);
CREATE INDEX idx_exercises_category_id ON exercises(category_id);