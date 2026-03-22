package br.com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // Configura o esquema de seguranca JWT no Swagger
        SecurityScheme jwtScheme = new SecurityScheme()
            .name("Authorization")
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

        return new OpenAPI()
            .info(new Info()
                .title("Clinica Medica API")
                .version("v1.0")
                .description("API REST para gestao de clinica medica")
                .contact(new Contact().name("Dev").email("dev@clinica.com")))
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new Components().addSecuritySchemes("JWT", jwtScheme));
    }
}

/*
 * Esta classe configura o Swagger (OpenAPI) para a aplicação, definindo as informações da API e o esquema de segurança
 * JWT para autenticação, O método openAPI() cria e retorna um objeto OpenAPI com as informações da API e a configuração
 * do esquema de segurança JWT, permitindo que os endpoints protegidos sejam testados diretamente no Swagger UI usando
 * um token JWT válido.
*/
