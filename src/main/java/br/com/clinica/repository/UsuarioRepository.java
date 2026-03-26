package br.com.clinica.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar usuário por email - Query derivada: 
    // Spring gera SELECT * FROM usuarios WHERE email = ?1
    // Usado pelo UserDetailsServiceImpl para autenticação e para vincular o usuário ao médico correspondente.
    Optional<Usuario> findByEmail(String email);

    // Método para verificar se um usuário com determinado email já existe - Query derivada: 
    // Spring gera SELECT COUNT(*) > 0 WHERE email = ?1 (valida duplicada no cadastro)
    boolean existsByEmail(String email);
    
}
