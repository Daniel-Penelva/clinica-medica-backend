package br.com.clinica.dto.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MedicoRequest(

        @NotBlank(message = "Nome é obrigatório") 
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres") 
        String nome,

        @NotBlank(message = "CRM é obrigatório") 
        @Pattern(regexp = "\\d{4,7}", message = "CRM deve conter entre 4 e 7 digitos numéricos") 
        String crm,

        @Email(message = "Email inválido") 
        String email,

        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 digitos") 
        String telefone,

        // IDS das especialidades que o médico possui
        // Exemplo: [1, 3] = Clinica Geral e Dermatologia
        List<Long> especialidadeIds) {}

/**
 * DTO de entrada para criação e atualização de Médico.
 * Especialidade informadas como lista de IDs (já existem no banco de dados)
*/
