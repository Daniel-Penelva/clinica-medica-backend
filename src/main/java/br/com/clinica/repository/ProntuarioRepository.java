package br.com.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Prontuario;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long>{

    /**
     * Busca prontuário pelo ID da consulta (relação 1:1).
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM prontuarios WHERE consulta_id = ?1</code></p>
     * 
     * @param consultaId ID da consulta
     * @return           Prontuário encontrado ou Optional vazio
     */
    Optional<Prontuario> findByConsultaId(Long consultaId);

    /**
     * Verifica se a consulta já possui prontuário associado.
     * 
     * <p>Query derivada para validação de existência:
     * <code>SELECT COUNT(*) > 0 FROM prontuarios WHERE consulta_id = ?1</code></p>
     * 
     * @param consultaId ID da consulta
     * @return           <code>true</code> se existe prontuário, <code>false</code> caso contrário
     */
    boolean existsByConsultaId(Long consultaId);
    
}
