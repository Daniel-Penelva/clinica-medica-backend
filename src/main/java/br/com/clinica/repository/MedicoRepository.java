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

        /**
         * Busca médicos ativos com paginação.
         * 
         * <p>Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT * FROM medicos WHERE ativo = true</code></p>
         * 
         * @param pageable Configurações de paginação
         * @return         Página de médicos ativos
         */
        Page<Medico> findByAtivoTrue(Pageable pageable);

        /**
         * Busca médico pelo CRM único.
         * 
         * <p>Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT * FROM medicos WHERE crm = ?1</code></p>
         * 
         * @param crm CRM do médico
         * @return    Médico encontrado ou Optional vazio
         */
        Optional<Medico> findByCrm(String crm);

        /**
         * Verifica se já existe médico com o CRM informado.
         * 
         * <p>Query derivada para validação de unicidade no cadastro:
         * <code>SELECT COUNT(*) > 0 FROM medicos WHERE crm = ?1</code></p>
         * 
         * @param crm CRM a ser validado
         * @return    <code>true</code> se CRM já existe, <code>false</code> caso contrário
         */
        boolean existsByCrm(String crm);

        /**
         * Verifica se já existe médico com o email informado.
         * 
         * <p>Query derivada para validação de unicidade no cadastro:
         * <code>SELECT COUNT(*) > 0 FROM medicos WHERE email = ?1</code></p>
         * 
         * @param email Email a ser validado
         * @return      <code>true</code> se email já existe, <code>false</code> caso contrário
         */
        boolean existsByEmail(String email);

        /**
         * Verifica se existe médico com email duplicado, ignorando o registro atual.
         * 
         * <p>Utilizado na validação de atualização, excluindo o próprio médico sendo editado.</p>
         * 
         * @param email ID do médico atual (a ser ignorado na busca)
         * @param id    Email a ser validado
         * @return      <code>true</code> se existe duplicata, <code>false</code> caso contrário
         */
        boolean existsByEmailAndIdNot(String email, Long id);

        /**
         * Busca médicos ativos por nome parcial (case-insensitive) com paginação.
         * 
         * <p>Query personalizada com LIKE para pesquisa fuzzy no nome completo.</p>
         * 
         * @param nome    Termo de busca no nome
         * @param pageable Configurações de paginação
         * @return        Página de médicos ativos que contenham o termo no nome
         */
        @Query("SELECT m FROM Medico m " +
                        "WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
                        "AND m.ativo = true")
        Page<Medico> buscarPorNome(@Param("nome") String nome, Pageable pageable);

        /**
         * Busca médicos ativos por especialidade.
         * 
         * <p>Query personalizada com JOIN para listar médicos que possuem a especialidade informada.</p>
         * 
         * @param especialidadeId ID da especialidade
         * @return                 Lista de médicos ativos com essa especialidade
         */
        @Query("SELECT m FROM Medico m JOIN m.especialidades e " +
                        "WHERE e.id = :especialidadeId AND m.ativo = true")
        List<Medico> findByEspecialidadeId(@Param("especialidadeId") Long especialidadeId);

        /**
         * Busca médico pelo email do usuário vinculado.
         * 
         * <p>Query personalizada com JOIN para autenticação/recuperação por email do sistema de usuários.</p>
         * 
         * @param email Email do usuário
         * @return      Médico vinculado ao usuário ou Optional vazio
         */
        @Query("SELECT m FROM Medico m JOIN Usuario u ON u.medico = m " +
                        "WHERE u.email = :email")
        Optional<Medico> findByUsuarioEmail(@Param("email") String email);

        /**
         * Adiciona o total de medicos ativos
         * @return total medicos ativos encontrados
         */
        long countByAtivoTrue();

}
