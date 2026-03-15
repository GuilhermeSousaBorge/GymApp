CREATE TABLE training_programs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    order_in_program INTEGER NOT NULL,
    trainer_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_training_program_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_training_program_trainer FOREIGN KEY (trainer_id) REFERENCES users(id)
);

CREATE INDEX idx_training_programs_user_id ON training_programs(user_id);
CREATE INDEX idx_training_programs_trainer_id ON training_programs(trainer_id);
CREATE INDEX idx_training_programs_name ON training_programs(name);