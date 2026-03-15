CREATE TABLE training_exercises (
                                    id BIGSERIAL PRIMARY KEY,
                                    training_sheet_id BIGINT NOT NULL,
                                    exercise_id BIGINT NOT NULL,
                                    sets INTEGER NOT NULL,
                                    reps VARCHAR(50) NOT NULL,
                                    order_in_sheet INTEGER,
                                    rest_time_in_seconds INTEGER,
                                    technique_notes VARCHAR(50),
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id),
                                    CONSTRAINT fk_training_sheet FOREIGN KEY (training_sheet_id) REFERENCES training_sheets(id)
);

CREATE TRIGGER trg_training_exercises_updated_at
    BEFORE UPDATE ON training_exercises
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();