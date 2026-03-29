package backend.training.port;

import backend.training.model.entity.TrainingProgram;

/**
 * PORTA: Training Program Validation
 *
 * Define contrato para operações de VALIDAÇÃO de programas de treinamento.
 * Responsável por verificar existência e unicidade de programas.
 *
 * Implementação: TrainingProgramRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de VALIDAÇÃO
 */
public interface TrainingProgramValidationPort {
    
    /**
     * Verifica se existe programa com nome e ID diferentes
     * (validação para update - garantir nome único)
     * @param name Nome do programa
     * @param id ID do programa (para excluir da validação)
     * @return true se existe, false caso contrário
     */
    boolean existsByNameAndIdDifferent(String name, Long id);
    
    /**
     * Verifica se existe programa com mesmo nome para um aluno
     * @param name Nome do programa
     * @param studentId ID do aluno
     * @return true se existe, false caso contrário
     */
    boolean existsByNameAndStudent(String name, Long studentId);
}

