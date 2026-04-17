package br.com.clinica.dto.response;

public record ConsultasPorEspecialidadeResponse(
        String especialidade,
        long total,
        double percentual // calculado no Service

) {}

/**
 * Consultas agrupadas por especialidade para grafico de pizza.
 */

