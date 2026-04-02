package br.com.clinica.dto.response;

public record EnderecoResponse(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep

) {}

/**
 * DTO endereco retornando nas respostas da API. Usado dentro do PacienteResponse.
 * Todos os campos são opcionais - endereco pode ser informado depois.
*/
