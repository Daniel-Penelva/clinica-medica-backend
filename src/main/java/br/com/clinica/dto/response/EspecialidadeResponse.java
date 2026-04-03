package br.com.clinica.dto.response;

public record EspecialidadeResponse(
        Long id,
        String nome) {}

/**
 * String de saída (resposta) para a Especialidade.
 * Retornado dentro do MedicoResponse e no endpoint de listagem
*/