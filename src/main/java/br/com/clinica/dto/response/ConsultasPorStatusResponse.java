package br.com.clinica.dto.response;

public record ConsultasPorStatusResponse(
        long agendadas,
        long confirmadas,
        long realizadas,
        long canceladas,
        long naoCompareceu

) {}

/**
 * Contagem de consultas agrupadas por status.
 * Usado para gráfico de rosca/pizza no Dashboard.
 */
