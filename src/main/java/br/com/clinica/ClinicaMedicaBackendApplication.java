package br.com.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class ClinicaMedicaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicaMedicaBackendApplication.class, args);
	}

}

/*  
 * @EnableJpaAuditing é uma anotação do Spring Data JPA que permite usar @CreatedDate e @LastModifiedDate 
 * para rastrear automaticamente as datas de criação e modificação das entidades (ou seja, para auditoria automática de datas).
 * 
 * @ConfigurationPropertiesScan é uma anotação do Spring Boot que habilita a varredura de classes anotadas com @ConfigurationProperties,
 * permitindo que as propriedades sejam mapeadas automaticamente a partir do application.yml ou application.properties, facilitando a 
 * configuração centralizada e a injeção de dependência para classes de configuração personalizada, como CorsProperties. 
 * */
