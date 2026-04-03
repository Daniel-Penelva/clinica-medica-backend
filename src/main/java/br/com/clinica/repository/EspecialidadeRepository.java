package br.com.clinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Especialidade;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long>{

    /**
     * Busca todas as especialidades cujo ID está na lista informada.
     * Usado no cadastro para vincular as especialidades ao médico
     * Spring gera: SELECT * FROM especialidades WHERE id IN(?, ?, ...)
    */
    List<Especialidade> findAllByIdIn(List<Long> ids);
    
}
