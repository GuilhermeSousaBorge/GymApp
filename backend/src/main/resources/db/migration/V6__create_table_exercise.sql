CREATE TABLE exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name varchar(100) not null unique,
    description text,
    category_id BIGINT,
    equipment varchar(100),
    active boolean default true,
    video_url varchar(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercise_category FOREIGN KEY (category_id) REFERENCES exercise_categories(id)
);

CREATE index idx_exercises_name on exercises(name);
CREATE index idx_exercises_category_id on exercises(category_id);