package br.com.clinica.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * Lista de origens permitidos para CORS, configurável via application.yml ou variáveis de ambiente (CORS ORIGINS).
     */
    private List<String> allowedOrigins = List.of("http://localhost:4200");

    // Getters e Setters
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
    
}

/*  
 * Esta classe é responsável por mapear as propriedades de CORS definidas no application.yml, permitindo que as origens permitidas 
 * sejam configuradas dinamicamente via variáveis de ambiente ou diretamente no arquivo de configuração. 
*/

