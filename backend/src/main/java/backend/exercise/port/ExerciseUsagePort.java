package backend.exercise.port;

/**
 * PORTA: Exercise Usage
 *
 * Contrato para validar se um exercicio esta sendo usado em contexto externo
 * (ex.: modulo de treino), sem acoplamento direto a repositories de outro modulo.
 */
public interface ExerciseUsagePort {

    boolean isExerciseInUse(Long exerciseId);
}

