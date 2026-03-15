CREATE TABLE training_sheets (
                                 id BIGSERIAL PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL,
                                 description TEXT NOT NULL,
                                 active BOOLEAN NOT NULL DEFAULT TRUE,
                                 rest_time_seconds INT,
                                 training_program_id BIGINT NOT NULL,
                                 order_in_program INT NOT NULL,
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_training_program FOREIGN KEY (training_program_id) REFERENCES training_programs(id)
);

CREATE TRIGGER trg_training_sheets_updated_at
    BEFORE UPDATE ON training_sheets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_training_sheets_name ON training_sheets(name);
CREATE INDEX idx_training_sheets_training_program_id ON training_sheets(training_program_id);


CREATE TABLE training_sheet_days (
                                     training_sheet_id BIGINT NOT NULL,
                                     day_of_week VARCHAR(20) NOT NULL, -- 0 = Sunday, 1 = Monday, ..., 6 = Saturday
                                     PRIMARY KEY (training_sheet_id, day_of_week),
                                     CONSTRAINT fk_training_sheet_days_training_sheet FOREIGN KEY (training_sheet_id) REFERENCES training_sheets(id)
);