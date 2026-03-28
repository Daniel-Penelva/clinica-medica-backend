package br.com.clinica.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /* ==== Implementação do método loadUserByUsername para integração com Spring Security =============================== */

    /**
     * Carrega os detalhes do usuário a partir do email (username) para autenticação.
     * @param email - email do usuário que está tentando se autenticar.
     * @return UserDetails contendo as informações do usuário para o Spring Security.
     * @throws UsernameNotFoundException se o usuário com o email fornecido não for encontrado no banco de dados.
     * 
     * O método utiliza o UsuarioRepository para buscar um usuário pelo email. Se o usuário for encontrado, ele é retornado como um objeto UserDetails (pois a entidade Usuario implementa UserDetails). 
     * Se o usuário não for encontrado, uma exceção UsernameNotFoundException é lançada, indicando que a autenticação falhou devido à ausência do usuário.
     */
    @Override
    @Transactional(readOnly = true) // Transação somente leitura para otimizar perfomance e garantir consistência dos dados durante a consulta.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));
    }
}

/* Está classe UserDetailsServiceImpl implementa UserDetailsService do Spring Security. 
 * Carrega o Usuario do banco pelo email para autenticação.
 * A entidade Usuario implementa UserDetails.
 * */ 
