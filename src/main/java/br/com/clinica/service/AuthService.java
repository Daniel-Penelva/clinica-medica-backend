package br.com.clinica.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import br.com.clinica.domain.model.Usuario;
import br.com.clinica.dto.request.LoginRequest;
import br.com.clinica.dto.response.JwtResponse;
import br.com.clinica.exception.BusinessException;
import br.com.clinica.repository.UsuarioRepository;
import br.com.clinica.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;


    /**
     * Autentica um usuário com email e senha e gera um token JWT de acesso e refresh token.
     * O authenticationManager delega a autenticação para o Spring Security, que busca o usuário (UserDetailsServiceImpl) e valida a senha e 
     * o BCryptPasswordEncoder que compara a senha.
     * @param request - objeto contendo email e senha do usuário a ser autenticado
     * @return JWTResponse - objeto contendo o token JWT de acesso, refresh token, role, email e tempo de expiração
     */
    public JwtResponse autenticar(LoginRequest request) {
        try {

            // Tenta autenticar: Spring Security busca e válida internamente o usuário e a senha
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
            
        } catch (AuthenticationException e) {
            // Captura BadCredentialsException, DisableException e outras exceções de autenticação
            throw new BusinessException("Email ou senha inválidos");
        }

        // Busca o usuário no banco de dados para gerar o token JWT
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow(
                () -> new BusinessException("Usuário não encontrado"));

        // Gera o token JWT de acesso (24h) e o refresh token (7 dias)
        String token = jwtTokenProvider.gerarToken(usuario);
        String refreshToken = jwtTokenProvider.gerarRefreshToken(usuario);

        // Retorna o novo token, refresh token, role, email e tempo de expiração para o cliente (24h = 86400000ms)
        return new JwtResponse(token, refreshToken, usuario.getRole().name(), usuario.getEmail(),86400000L);
    }


    /**
     * Renova o token JWT de acesso usando um refresh token válido. O refresh token é um token de longa duração (7 dias) que 
     * pode ser usado para obter um novo token de acesso sem precisar reautenticar o usuário.
     * @param refreshToken - token de atualização enviado pelo cliente para renovar o token de acesso
     * @return JWTResponse - objeto contendo o token JWT de acesso, refresh token, role, email e tempo de expiração
     */
    public JwtResponse renovarToken(String refreshToken) {

        // 1. variável para armazenar email extraído do refresh token
        String email = jwtTokenProvider.extrairEmail(refreshToken);

        // 2. Se o email for nulo, lança exceção de usuário não encontrado
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException("Usuário não encontrado."));

        // 3. Verifica se o refresh token é válido para o usuário 
        if (!jwtTokenProvider.isTokenValido(refreshToken, usuario)) {
            throw new BusinessException("Refresh token inválido ou expirado");
        }

        // 4. Gera um novo token de acesso e um novo refresh token
        String novoToken = jwtTokenProvider.gerarToken(usuario);
        String novoRefreshToken = jwtTokenProvider.gerarRefreshToken(usuario);

        // 5. Retorna o novo token, refresh token, role, email e tempo de expiração para o cliente (24h = 86400000ms)
        return new JwtResponse(novoToken, novoRefreshToken, usuario.getRole().name(), usuario.getEmail(), 86400000L);
    }
    
}

/**
 * Camada de Serviço - Está classe é responsável por implementar a lógica de autenticação e renovação de tokens JWT. 
 * Ela utiliza o AuthenticationManager do Spring Security para autenticar o usuário com email e senha, e o JwtTokenProvider para gerar e validar 
 * os tokens JWT.
 * 
*/
