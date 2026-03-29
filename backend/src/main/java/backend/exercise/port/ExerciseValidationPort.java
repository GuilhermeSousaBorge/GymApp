package backend.exercise.port;

/**
 * PORTA: Exercise Validation
 *
 * Define contrato para operações de VALIDAÇÃO de exercícios.
 * Responsável por verificar existência e unicidade.
 *
 * Implementação: ExerciseRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de VALIDAÇÃO
 */
public interface ExerciseValidationPort {
    
    /**
     * Verifica se exercício com nome já existe
     * @param name Nome do exercício
     * @return true se existe, false caso contrário
     */
    boolean existsByName(String name);
}

