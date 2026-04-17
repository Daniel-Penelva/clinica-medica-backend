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


    /**
     * Busca pacientes ativos com paginação.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM pacientes WHERE ativo = true</code></p>
     * 
     * @param pageable Configurações de paginação
     * @return         Página de pacientes ativos
     */
    Page<Paciente> findByAtivoTrue(Pageable pageable);

    /**
     * Busca paciente pelo CPF único.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM pacientes WHERE cpf = ?1</code></p>
     * 
     * @param cpf CPF do paciente
     * @return    Paciente encontrado ou Optional vazio
     */
    Optional<Paciente> findByCpf(String cpf);

    /**
     * Verifica se já existe paciente com o CPF informado.
     * 
     * <p>Query derivada para validação de unicidade no cadastro:
     * <code>SELECT COUNT(*) > 0 FROM pacientes WHERE cpf = ?1</code></p>
     * 
     * @param cpf CPF a ser validado
     * @return    <code>true</code> se CPF já existe, <code>false</code> caso contrário
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se já existe paciente com o email informado.
     * 
     * <p>Query derivada para validação de unicidade no cadastro:
     * <code>SELECT COUNT(*) > 0 FROM pacientes WHERE email = ?1</code></p>
     * 
     * @param email Email a ser validado
     * @return      <code>true</code> se email já existe, <code>false</code> caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica email duplicado na atualização (ignora o próprio registro).
     * 
     * <p>Query derivada para validação de atualização:
     * <code>SELECT COUNT(*) > 0 FROM pacientes WHERE email = ?1 AND id != ?2</code></p>
     * 
     * <p>Garante que o email seja único entre outros pacientes, excluindo o paciente 
     * sendo atualizado identificado pelo ID.</p>
     * 
     * @param email Email a ser validado
     * @param id    ID do paciente atual (a ser ignorado)
     * @return      <code>true</code> se existe duplicata, <code>false</code> caso contrário
     */
    boolean existsByEmailAndIdNot(String email, long id);

    /**
     * Busca pacientes ativos por nome parcial (case-insensitive) com paginação.
     * 
     * <p>Query personalizada com LIKE para pesquisa fuzzy no nome completo.</p>
     * 
     * @param nome    Termo de busca no nome
     * @param pageable Configurações de paginação
     * @return        Página de pacientes ativos que contenham o termo no nome
     */
    @Query("SELECT p FROM Paciente p " +
            "WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
            "AND p.ativo = true")
    Page<Paciente> buscarPorNome(@Param("nome") String nome, Pageable pageable);

    /**
     * Adiciona total de pacientes ativos
     * @return total pacientes ativos encontrados
     */
    long countByAtivoTrue();

}
