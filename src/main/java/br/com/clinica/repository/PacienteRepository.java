package br.com.clinica.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Métodos para buscar pacientes ativos - Query derivada: 
    // Spring gera SELECT * FROM pacientes WHERE ativo = true
    Page<Paciente> findByAtivoTrue(Pageable pageable);

    // Método para buscar paciente por CPF - Query derivada: 
    // Spring gera SELECT * FROM pacientes WHERE cpf = ?1
    Optional<Paciente> findByCpf(String cpf);

    // Método para verificar se um paciente com determinado CPF já existe - Query derivada: 
    // Spring gera SELECT COUNT(*) > 0 WHERE cpf = ?1 (valida duplicada no cadastro)
    boolean existsByCpf(String cpf);

    // Método para verificar se um paciente com determinado email já existe,
    // excluindo o paciente atual (útil para validação de email único no cadastro e atualização)
    // Usado na atualização para não conflitar com o próprio registro.
    // Query derivada: Spring gera SELECT COUNT(*) > 0 WHERE email = ?1 AND id != ?2
    boolean existsByEmailAndIdNot(String email, long id);

    // Método para buscar pacientes por nome (com paginação)
    @Query("SELECT p FROM Paciente p " +
            "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
            "AND p.ativo = true")
    Page<Paciente> buscarPorNome(@Param("nome") String nome, Pageable pageable);

}
