CREATE TABLE training_exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    training_sheet_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    sets INTEGER NOT NULL,
    reps varchar(50) NOT NULL,
    order_in_training INTEGER,
    rest_time_in_seconds INTEGER,
    technique_notes VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id),
    CONSTRAINT fk_training_Sheet FOREIGN KEY (training_sheet_id) REFERENCES training_sheets(id)
)