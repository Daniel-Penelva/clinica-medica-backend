package br.com.clinica.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // Método para buscar médicos ativos - Query derivada: 
    // Spring gera SELECT * FROM medicos WHERE ativo = true
    Page<Medico> findByAtivoTrue(Pageable pageable);

    // Método para buscar médico por CRM - Query derivada: 
    // Spring gera SELECT * FROM medicos WHERE crm = ?1
    Optional<Medico> findByCrm(String crm);

    // Método para verificar se um médico com determinado CRM já existe - Query
    // derivada: Spring gera SELECT COUNT(*) > 0 WHERE crm = ?1 (valida duplicada no cadastro)
    boolean existsByCrm(String crm);

    // Método para buscar medicos ativos por especialidade
    @Query("SELECT m FROM Medico m JOIN m.especialidades e " +
            "WHERE e.id = :especialidadeId AND m.ativo = true")
    List<Medico> findByEspecialidadeId(@Param("especialidadeId") Long especialidadeId);

    // Método para buscar médico pelo email do usuário vinculado
    @Query("SELECT m FROM Medico m JOIN Usuario u ON u.medico = m " +
            "WHERE u.email = :email")
    Optional<Medico> findByUsuarioEmail(@Param("email") String email);

}
