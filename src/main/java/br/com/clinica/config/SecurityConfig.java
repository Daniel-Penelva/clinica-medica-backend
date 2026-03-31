package br.com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.clinica.security.JwtAuthFilter;
import br.com.clinica.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize nos controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Configura as regras de segurança para as requisições HTTP.
     * 
     * @param http - valor do HttpSecurity para configurar as regras de segurança.
     * @return - valor do SecurityFilterChain configurado com as regras de segurança
     *         definidas.
     * @throws Exception
     * 
     * <p>Configurações de Segurança</p>
     * <ul>
     *    <li> .cors(cors -> cors.configure(http)) - Habilita o CORS usando o CorsFilter registrado no GlobalCorsConfig  </li>
     *    <li> .csrf(csrf -> csrf.disable()) - Desabilita a proteção CSRF, o que é comum em APIs RESTful que não mantêm estado. </li>
     *    <li> .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) - Configura a política de criação de sessão para STATELESS, indicando que a aplicação não deve criar ou usar sessões HTTP. </li>
     *    <li> .authorizeHttpRequests(auth -> auth
     *            .requestMatchers("/api/v1/auth/**").permitAll() - Permite acesso sem autenticação para endpoints relacionados à páginas como login, registro, etc.
     *            .requestMatchers("/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**").permitAll() - Permite acesso sem autenticação para os endpoints relacionados à documentação da API (Swagger).
     *            .anyRequest().authenticated()) - Exige autenticação para todas as outras requisições. 
     *    </li>
     *    <li> .authenticationProvider(authenticationProvider()) - Configura o AuthenticationProvider personalizado para autenticação. </li>
     *    <li> .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) - Adiciona o filtro de autenticação JWT antes do filtro de autenticação padrão do Spring Security. </li>
     *    <li> .build() - Constrói o SecurityFilterChain com as configurações definidas. </li>
     * </ul>
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    /**
     * Configura o AuthenticationProvider para usar o DaoAuthenticaionProvider, que autentica os usuários usando um  UserDetailsService e um PasswordEncoder.
     * Provedor de autenticação que usa o UserDetailsService para carregar os detalhes do usuário e o PasswordEncoder para verificar as senhas.
     * @return AuthenticationProvider configurado para usar o DaoAuthenticationProvider com o UserDetailsService e PasswordEncoder definidos.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configura o AuthenticationManager para ser usado na autenticação de usuários.
     * Expõe o AuthenticationManager como bean para o AuthService.
     * @param config - valor do AuthenticationConfiguration para obter o AuthenticationManager configurado.
     * @return AuthenticationManager obtido do AuthenticationConfiguration.
     * @throws Exception - se ocorrer um erro ao obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    /**
     * Configura o PasswordEncoder para usar o BCrypt (strength) com um fator de força de 10: ~100ms por hash
     * Seguro contra brute forcesem ser lento para o usuário.
     * @return PasswordEncoder configurado para usar BCrypt com strength 10.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}

/**
 * Está classe é responsável por configurar a segurança da aplicação usando Spring Security. Ela define as regras de segurança para as
 * requisições HTTP, o provedor de autenticação, o gerenciador de autenticação e o codificador de senhas.
 * A configuração inclui a habilitação do CORS, a desabilitação do CSRF, a configuração de uma política de criação de sessão sem estado, a definição de quais endpoints são públicos e quais exigem autenticação, e a adição de um filtro de autenticação JWT.
 * O uso de @EnableMethodSecurity permite o uso de anotações como @PreAuthorization nos controllers para controle de acesso baseado em métodos.
*/
