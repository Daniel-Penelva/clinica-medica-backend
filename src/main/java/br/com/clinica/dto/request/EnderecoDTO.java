package br.com.clinica.dto.request;

public record EnderecoDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep) {}

/**
 * DTO de endereco usado dentro do PacienteRequest.
 * Todos os campos são opcionais - endereco pode ser informado depois.
*/
