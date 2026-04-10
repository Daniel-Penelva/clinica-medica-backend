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
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM consultas WHERE paciente_id = ?1</code></p>
     * 
     * @param pacienteId ID do paciente
     * @param pageable   Configurações de paginação
     * @return           Página de consultas do paciente
     */
    Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);


    /**
     * Busca consultas paginadas de um médico específico.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM consultas WHERE medico_id = ?1</code></p>
     * 
     * @param medicoId ID do médico
     * @param pageable Configurações de paginação
     * @return         Página de consultas do médico
     */
    Page<Consulta> findByMedicoId(Long medicoId, Pageable pageable);

    /**
     * Busca consultas paginadas por status específico.
     * 
     * <p>Query derivada gerada pelo Spring Data JPA:
     * <code>SELECT * FROM consultas WHERE status = ?1</code></p>
     * 
     * @param status   Status da consulta
     * @param pageable Configurações de paginação
     * @return         Página de consultas com o status informado
     */
    Page<Consulta> findByStatus(StatusConsulta status, Pageable pageable);


    /**
     * Verifica se existe conflito de horário para agendamento.
     * 
     * <p>Query personalizada que verifica se já existe consulta para o mesmo
     * médico no mesmo horário, excluindo consultas canceladas ou com não comparecimento.</p>
     * 
     * @param medicoId ID do médico
     * @param dataHora Data e hora proposta para o agendamento
     * @return         <code>true</code> se existe conflito, <code>false</code> caso contrário
     */
    @Query("SELECT COUNT(c) > 0 FROM Consulta c " +
            "WHERE c.medico.id = :medicoId " +
            "AND c.dataHora = :dataHora " +
            "AND c.status NOT IN ('CANCELADA', 'NAO_COMPARECEU')")
    boolean existeConflito(@Param("medicoId") Long medicoId, @Param("dataHora") LocalDateTime dataHora);

    /**
     * Lista consultas agendadas de um médico para um dia específico no dashboard.
     * 
     * <p>Query personalizada que busca consultas no intervalo de tempo informado,
     * filtrando apenas status AGENDADA e ordenando por data/hora ascendente.</p>
     * 
     * @param inicio Início do intervalo de tempo (geralmente 00:00 do dia)
     * @param fim    Fim do intervalo de tempo (geralmente 23:59 do dia)
     * @return       Lista ordenada de consultas do período
     */
    @Query("SELECT c FROM Consulta c " +
            "WHERE c.dataHora BETWEEN :inicio AND :fim " +
            "AND c.status = 'AGENDADA' ORDER BY c.dataHora ASC")
    List<Consulta> findConsultasDoDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

}
