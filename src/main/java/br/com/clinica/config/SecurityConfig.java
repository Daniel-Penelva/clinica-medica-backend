package br.com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura as regras de segurança para as requisições HTTP.
     * 
     * @param http - valor do HttpSecurity para configurar as regras de segurança.
     * @return - valor do SecurityFilterChain configurado com as regras de segurança definidas.
     * @throws Exception
     * 
     * <p>Configurações de Segurança</p>
     * <ul>
     *    <li> .cors(cors -> cors.configure(http)) - Habilita o CORS usando o CorsFilter registrado no GlobalCorsConfig  </li>
     *    <li> .csrf(csrf -> csrf.disable()) - Desabilita a proteção CSRF, o que é comum em APIs RESTful que não mantêm estado. </li>
     *    <li> .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) - Configura a política de criação de sessão para STATELESS, indicando que a aplicação não deve criar ou usar sessões HTTP. </li>
     *    <li> .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) - Permite todas as requisições sem exigir autenticação. </li>
     * </ul>
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
    
}
