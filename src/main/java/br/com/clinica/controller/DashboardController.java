package br.com.clinica.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.response.ConsultaHojeResponse;
import br.com.clinica.dto.response.ConsultasPorEspecialidadeResponse;
import br.com.clinica.dto.response.ConsultasPorMesResponse;
import br.com.clinica.dto.response.ConsultasPorStatusResponse;
import br.com.clinica.dto.response.DashboardResumoResponse;
import br.com.clinica.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Estatisticas e relatorios da clinica")
public class DashboardController {

    private final DashboardService dashboardService;
    
    // --- GET: Listar Resumo ----------------------------------------------------

    /**
     * Retorna cards principais do dashboard (KPIs gerais).
     * 
     * <p>Métricas incluídas:</p>
     * <ul>
     * <li>Total pacientes ativos</li>
     * <li>Total médicos ativos</li>
     * <li>Consultas hoje</li>
     * <li>Consultas no mês</li>
     * <li>Consultas agendadas</li>
     * <li>Consultas realizadas no mês</li>
     * </ul>
     * 
     * @return Resumo completo dos KPIs da clínica
     */
    @GetMapping("/resumo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Resumo geral - cards do dashboard", description = "Retorna um resumo geral com total de pacientes ativos, total de médicos ativos, total de consultas agendadas para o dia e total de consultas realizadas no mês.")
    public DashboardResumoResponse getResumo() {
        return dashboardService.getResumo();
    }

    // --- GET: Listar Consultas de Hoje ----------------------------------------------------

    /**
     * Lista consultas agendadas para hoje (00:00 - 23:59).
     * 
     * @return Lista ordenada das consultas de hoje com paciente, médico e status
     */
    @GetMapping("/consultas-hoje")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultas agendadas para hoje", description = "Retorna uma lista de consultas agendadas para o dia atual, incluindo informações do paciente, médico, horário e status da consulta.")
    public List<ConsultaHojeResponse> getConsultasHoje() {
        return dashboardService.getConsultasHoje();
    }

    // --- GET: Listar número de consultas por Status ----------------------------------------------------

    /**
     * Retorna contagem de consultas por status.
     * 
     * <p>Status incluídos: AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, NÃO COMPARECEU.</p>
     * 
     * @return Totais agrupados por cada status da consulta
     */
    @GetMapping("/por-status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultas agrupadas por status", description = "Contagem total de consultas por status: " + "Agendada, Confirmada, Realizada, Cancelada, Não Compareceu.")
    public ConsultasPorStatusResponse getConsultasPorStatus() {
        return dashboardService.getConsultasPorStatus();
    }


    // --- GET: Listar número de consultas por Mês para os últimos 6 meses ----------------------------------------------------

    /**
     * Retorna evolução mensal de consultas (últimos 6 meses).
     * 
     * <p>Formato: "Jan/2026", "Fev/2026", etc. com totais mensais.</p>
     * 
     * @return Lista ordenada [mês/ano, total] dos últimos 6 meses
     */
    @GetMapping("/por-mes")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Consultas dos ultimos 6 meses", 
        description = "Evolução mensal dos últimos 6 meses com nomes em português " + "(Jan/2026, Fev/2026, etc.) e total de consultas.")
    public List<ConsultasPorMesResponse> getConsultasPorMes() {
        return dashboardService.getConsultasPorMes();
    }

    // --- GET: Listar consultas por especialidade ----------------------------------------------------

    /**
     * Retorna ranking de consultas por especialidade com percentuais.
     * 
     * <p>Ordenado por volume DESC com cálculo de percentual sobre total geral.</p>
     * 
     * @return Lista [especialidade, total, percentual%] ordenada por volume
     */
    @GetMapping("/por-especialidade")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Consultas agrupadas por especialidade", 
        description = "Ranking das especialidades mais consultadas com total " + "e percentual sobre o total geral, ordenado por volume DESC.")
    public List<ConsultasPorEspecialidadeResponse> getConsultasPorEspecialidade() {
        return dashboardService.getConsultasPorEspecialidade();
    }


    // --- GET: Listar consultas das próximas consultas ----------------------------------------------------

    /**
     * Lista próximas consultas dos próximos 7 dias.
     * 
     * <p>Filtra apenas AGENDADA/CONFIRMADA, ordenadas por horário.</p>
     * 
     * @return Lista das próximas 7 dias de consultas ativas
     */
    @GetMapping("/proximas")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Proximas consultas (proximos 7 dias)", 
        description = "Agenda das próximas 7 dias com consultas AGENDADAS/CONFIRMADAS, " + "ordenadas por horário com paciente, médico e especialidade.")
    public List<ConsultaHojeResponse> getProximasConsultas() {
        return dashboardService.getProximasConsultas();
    }
}
