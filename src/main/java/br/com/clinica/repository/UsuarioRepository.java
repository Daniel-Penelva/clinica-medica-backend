package br.com.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário pelo email único.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM usuarios WHERE email = ?1</code></p>
     * 
     * <p>Utilizado principalmente pelo UserDetailsServiceImpl para autenticação
     * e para vincular o usuário ao médico correspondente.</p>
     * 
     * @param email Email do usuário
     * @return      Usuário encontrado ou Optional vazio
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se já existe usuário com o email informado.
     * 
     * <p>Query derivada para validação de unicidade no cadastro:
     * <code>SELECT COUNT(*) > 0 FROM usuarios WHERE email = ?1</code></p>
     * 
     * @param email Email a ser validado
     * @return      <code>true</code> se email já existe, <code>false</code> caso contrário
     */
    boolean existsByEmail(String email);
    
}
