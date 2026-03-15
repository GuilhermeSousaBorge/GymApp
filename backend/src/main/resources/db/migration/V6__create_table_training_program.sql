CREATE TABLE training_programs (
                                   id BIGSERIAL PRIMARY KEY,
                                   name VARCHAR(150) NOT NULL,
                                   description TEXT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   trainer_id BIGINT,
                                   active BOOLEAN NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   CONSTRAINT fk_training_program_user FOREIGN KEY (user_id) REFERENCES users(id),
                                   CONSTRAINT fk_training_program_trainer FOREIGN KEY (trainer_id) REFERENCES users(id)
);

CREATE TRIGGER trg_training_programs_updated_at
    BEFORE UPDATE ON training_programs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_training_programs_user_id ON training_programs(user_id);
CREATE INDEX idx_training_programs_trainer_id ON training_programs(trainer_id);
CREATE INDEX idx_training_programs_name ON training_programs(name);