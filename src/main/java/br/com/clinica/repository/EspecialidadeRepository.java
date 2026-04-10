package br.com.clinica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Especialidade;

@Repository
public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long>{


   /**
     * Busca especialidades pelos IDs informados.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA para recuperação em lote:
     * <code>SELECT * FROM especialidades WHERE id IN (?, ?, ...)</code></p>
     * 
     * <p>Utilizado principalmente no cadastro/atualização de médicos para
     * vincular múltiplas especialidades de forma eficiente em uma única consulta.</p>
     * 
     * @param ids Lista de IDs das especialidades
     * @return    Lista de especialidades encontradas
     */
    List<Especialidade> findAllByIdIn(List<Long> ids);
    
}
