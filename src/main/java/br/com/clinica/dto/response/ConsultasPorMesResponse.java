package br.com.clinica.dto.response;

public record ConsultasPorMesResponse(
        String mes, // ex: 'Jan/2026', 'Fev/2026'
        int ano,
        int numeroMes, // 1-12 para ordenacao no frontend
        long total

) {}

/**
 * Consultas agrupadas por mês para gráfico de barras
 * Retorna mês abreviado (Jan, Fev, ...) e total.
 */