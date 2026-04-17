package br.com.clinica.dto.response;

public record DashboardResumoResponse(
        long totalPacientesAtivos,
        long totalMedicosAtivos,
        long consultasHoje,
        long consultasMesAtual,
        long consultasAgendadas,
        long consultasRealizadasMes

) {}

/**
 * Dados para os cards de resumo do Dashboard.
 * Retorna totais gerais de uma só vez (evita 4 requisições separadas).
 */
