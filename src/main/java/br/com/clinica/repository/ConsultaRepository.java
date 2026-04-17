package br.com.clinica.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.clinica.domain.enums.StatusConsulta;
import br.com.clinica.domain.model.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

        /**
         * Busca consultas paginadas de um paciente específico.
         * 
         * <p>
         * Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT * FROM consultas WHERE paciente_id = ?1</code>
         * </p>
         * 
         * @param pacienteId ID do paciente
         * @param pageable   Configurações de paginação
         * @return Página de consultas do paciente
         */
        Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);

        /**
         * Busca consultas paginadas de um médico específico.
         * 
         * <p>
         * Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT * FROM consultas WHERE medico_id = ?1</code>
         * </p>
         * 
         * @param medicoId ID do médico
         * @param pageable Configurações de paginação
         * @return Página de consultas do médico
         */
        Page<Consulta> findByMedicoId(Long medicoId, Pageable pageable);

        /**
         * Busca consultas paginadas por status específico.
         * 
         * <p>
         * Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT * FROM consultas WHERE status = ?1</code>
         * </p>
         * 
         * @param status   Status da consulta
         * @param pageable Configurações de paginação
         * @return Página de consultas com o status informado
         */
        Page<Consulta> findByStatus(StatusConsulta status, Pageable pageable);

        /**
         * Verifica se existe conflito de horário para agendamento.
         * 
         * <p>
         * Query personalizada que verifica se já existe consulta para o mesmo
         * médico no mesmo horário, excluindo consultas canceladas ou com não
         * comparecimento.
         * </p>
         * 
         * @param medicoId ID do médico
         * @param dataHora Data e hora proposta para o agendamento
         * @return <code>true</code> se existe conflito, <code>false</code> caso
         *         contrário
         */
        @Query("SELECT COUNT(c) > 0 FROM Consulta c " +
                        "WHERE c.medico.id = :medicoId " +
                        "AND c.dataHora = :dataHora " +
                        "AND c.status NOT IN ('CANCELADA', 'NAO_COMPARECEU')")
        boolean existeConflito(@Param("medicoId") Long medicoId, @Param("dataHora") LocalDateTime dataHora);

        /**
         * Lista consultas agendadas de um médico para um dia específico no dashboard.
         * 
         * <p>
         * Query personalizada que busca consultas no intervalo de tempo informado,
         * filtrando apenas status AGENDADA e ordenando por data/hora ascendente.
         * </p>
         * 
         * @param inicio Início do intervalo de tempo (geralmente 00:00 do dia)
         * @param fim    Fim do intervalo de tempo (geralmente 23:59 do dia)
         * @return Lista ordenada de consultas do período
         */
        @Query("SELECT c FROM Consulta c " +
                        "WHERE c.dataHora BETWEEN :inicio AND :fim " +
                        "AND c.status = 'AGENDADA' ORDER BY c.dataHora ASC")
        List<Consulta> findConsultasDoDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

        /**
         * Conta total de consultas por status específico.
         * 
         * <p>Query derivada gerada pelo Spring Data JPA:
         * <code>SELECT COUNT(*) FROM consultas WHERE status = ?1</code></p>
         * 
         * <p>Utilizado em dashboards e relatórios para métricas por status.</p>
         * 
         * @param status Status da consulta (AGENDADA, CONFIRMADA, etc.)
         * @return       Total de consultas com o status informado
         */
        long countByStatus(StatusConsulta status);

        /**
         * Busca consultas realizadas entre duas datas.
         * 
         * <p>Query personalizada para relatórios mensais/periodo:
         * <code>SELECT c FROM Consulta c WHERE c.dataHora BETWEEN :inicio AND :fim</code></p>
         * 
         * @param inicio Data/hora inicial do período
         * @param fim    Data/hora final do período
         * @return       Lista de consultas no intervalo de tempo
         */
        @Query("SELECT c FROM Consulta c " +
                        "WHERE c.dataHora BETWEEN :inicio AND :fim")
        List<Consulta> findByDataHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

        /**
         * Agrupa consultas por mês/ano dos últimos 6 meses.
         * 
         * <p>Query analítica para dashboard de evolução mensal:
         * Retorna [mês, ano, total] ordenado cronologicamente.</p>
         * 
         * <p>Exemplo resultado: [[9, 2025, 45], [10, 2025, 52], ...]</p>
         * 
         * @param dataInicio Data inicial para cálculo (geralmente 6 meses atrás)
         * @return           Lista de Object[] com [mes, ano, total]
         */
        @Query("SELECT MONTH(c.dataHora) as mes, " +
                        "YEAR(c.dataHora) as ano, " +
                        "COUNT(c) as total " +
                        "FROM Consulta c " +
                        "WHERE c.dataHora >= :dataInicio " +
                        "GROUP BY YEAR(c.dataHora), MONTH(c.dataHora) " +
                        "ORDER BY YEAR(c.dataHora), MONTH(c.dataHora)")
        List<Object[]> countByMes(@Param("dataInicio") LocalDateTime dataInicio);

        /**
         * Agrupa consultas por especialidade dos médicos (ranking).
         * 
         * <p>Query analítica com JOINs múltiplos para dashboard:
         * Retorna [nome_especialidade, total_consultas] ordenado por volume DESC.</p>
         * 
         * <p>Exemplo resultado: [["Cardiologia", 125], ["Ortopedia", 98], ...]</p>
         * 
         * @return Lista de Object[] com [especialidade, total]
         */
        @Query("SELECT e.nome as especialidade, COUNT(c) as total " +
                        "FROM Consulta c " +
                        "JOIN c.medico m " +
                        "JOIN m.especialidades e " +
                        "GROUP BY e.nome " +
                        "ORDER BY COUNT(c) DESC")
        List<Object[]> countByEspecialidade();

        /**
         * Busca próximas consultas agendadas (próximos 7 dias).
         * 
         * <p>Query para dashboard médico/recepção:
         * Filtra apenas AGENDADA/CONFIRMADA nos próximos 7 dias, ordenado por horário.</p>
         * 
         * @param agora  Data/hora atual
         * @param limite Data/hora limite (geralmente agora + 7 dias)
         * @return       Lista ordenada das próximas consultas
         */
        @Query("SELECT c FROM Consulta c " +
                        "WHERE c.dataHora BETWEEN :agora AND :limite " +
                        "AND c.status IN ('AGENDADA', 'CONFIRMADA') " +
                        "ORDER BY c.dataHora ASC")
        List<Consulta> findProximasConsultas(@Param("agora") LocalDateTime agora, @Param("limite") LocalDateTime limite);

}
