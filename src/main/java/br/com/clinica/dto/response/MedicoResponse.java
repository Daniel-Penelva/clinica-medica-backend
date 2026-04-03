package br.com.clinica.dto.response;

import java.util.List;

public record MedicoResponse(
        Long id,
        String nome,
        String crm,
        String email,
        String telefone,
        Boolean ativo,
        List<EspecialidadeResponse> especialidades) {}

/**
 * DTO de saída para Medico.
 * Inclui a lista de especialidades mapeadas (não apenas os IDs).
 */

