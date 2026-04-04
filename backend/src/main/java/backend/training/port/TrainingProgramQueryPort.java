package backend.training.port;

import backend.training.model.entity.TrainingProgram;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: Training Program Query (Leitura)
 *
 * Define contrato para operações de LEITURA de programas de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingProgramRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 */
public interface TrainingProgramQueryPort {
    
    /**
     * Busca programa de treinamento por ID
     * @param id ID do programa
     * @return Optional contendo programa ou vazio
     */
    Optional<TrainingProgram> findById(Long id);
    
    /**
     * Busca programa com sheets carregadas (fetch eager)
     * @param id ID do programa
     * @return Optional contendo programa com sheets ou vazio
     */
    Optional<TrainingProgram> findByIdWithSheets(Long id);
    
    /**
     * Busca programa por nome
     * @param name Nome do programa
     * @return Optional contendo programa ou vazio
     */
    Optional<TrainingProgram> findByName(String name);
    
    /**
     * Lista programas de um aluno
     * @param studentId ID do aluno
     * @return Lista de programas
     */
    List<TrainingProgram> findByStudentId(Long studentId);
    
    /**
     * Busca programa de um trainer/instrutor
     * @param trainerId ID do trainer
     * @return Optional contendo programa ou vazio
     */
    Optional<TrainingProgram> findByTrainerId(Long trainerId);
    
    /**
     * Conta programas ativos
     * @return Quantidade de programas ativos
     */
    int countActive();
    
    /**
     * Lista todos os programas
     * @return Lista de programas
     */
    List<TrainingProgram> findAll();
}

