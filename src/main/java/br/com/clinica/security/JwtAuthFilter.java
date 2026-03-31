package br.com.clinica.security;

import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    
    
    /** 
     * Filtro que intercepta cada requisição HTTP para validar o token JWT presente no header Authorization.
     * 
     * @param request - Requisição HTTP recebida
     * @param response - Resposta HTTP a ser enviada
     * @param filterChain - Cadeia de filtros do Spring Security
     * @throws ServletException - Exceção lançada em caso de erro no processamento do filtro
     * @throws IOException - Exceção lançado em caso de erro de I/O durante o processamento do filtro
     * 
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1.  Extrai o header Authorization da requisição
        final String authHeader = request.getHeader("Authorization");

        // 2. Se não tem header ou não começa com 'Bearer' então passa adiante para o próximo filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Remove o prefixo 'Bearer ' para obter apenas o token JWT
        final String token = authHeader.substring(7);

        // 4. Extrai o email (subject) do payload do token
        final String email = jwtTokenProvider.extrairEmail(token);

        // 5. Se tem email E ainda não está autenticado nesta requisição
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Busca o usuário no banco de dados pelo email
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Valida: assinatura correta + não expirado + email do token bate com email do usuário
            if (jwtTokenProvider.isTokenValido(token, userDetails)) {

                // 8. Cria o objeto de autenticação com as roles do usuário
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 9. Adiciona detalhes da requisição (IP, session, etc) ao objeto de autenticação
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Registra autenticação no contexto da requisição atual
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continua a cadeia de filtros -> vai para o controller
        filterChain.doFilter(request, response);

    }
    
}

/**
 * Está classe é um filtro de autenticação JWT que intercepta cada requisição HTTP para validar o token JWT presente no header Authorization.
 * Filtro JWT exceutado uma vez por requisição (OncePerRequestFilter) que intercepta as requisições, extrai o token JWT do header, valida o 
 * token e, se válido, autentica o usuário no contexto (SecurityContext) de segurança do Spring. 
 * 
 * A classe OncePerRequestFilter é uma classe abstrata do Spring Security que garante que o filtro seja executado apenas uma vez por requisição,
 * evitando multiplas exceções ou processamento desnecessário. O método doFilterInternal é onde a lógica de validação do token JWT é implementado.
*/
