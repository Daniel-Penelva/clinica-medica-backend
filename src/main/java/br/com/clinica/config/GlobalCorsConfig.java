package br.com.clinica.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    private final CorsProperties corsProperties;

    /**
     *  Injeta as propriedades de CORS via construtor
     * @param corsProperties propriedades mapeadas do application.yml para CORS
     */
    public GlobalCorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    /**
     * Configura o filtro de CORS global para a aplicação, utilizando as propriedades definidas em CorsProperties.
     * @return filtro de CORS configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        config.setAllowCredentials(true);
        config.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );
        config.setAllowedHeaders(
            List.of("Authorization", "Content-Type", "X-Requested-With")
        );
        config.setExposedHeaders(
            List.of("Authorization")
        );
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}

/* 
 * Esta classe é responsável por configurar o filtro de CORS global para a aplicação, utilizando as propriedades definidas em CorsProperties.
 * Ela permite que as origens permitidas sejam configuradas dinamicamente via variáveis de ambiente ou diretamente no arquivo de configuração,
 * garantindo que a aplicação possa ser facilmente adaptada para diferentes ambientes (desenvolvimento, produção, etc.) sem a necessidade de alteração de código.
*/
