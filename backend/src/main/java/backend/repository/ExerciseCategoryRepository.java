package backend.repository;

import backend.model.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Long> {
    Optional<ExerciseCategory> findByMuscleGroup(String muscleGroup);

    boolean existsByMuscleGroup(String muscleGroup);

    List<ExerciseCategory> findByActiveTrue();

}
