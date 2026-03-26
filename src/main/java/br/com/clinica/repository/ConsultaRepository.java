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

    // Método para buscar consultas por paciente com paginação - Query derivada:
    // Spring gera SELECT * FROM consultas WHERE paciente_id = ?1
    Page<Consulta> findByPacienteId(Long pacienteId, Pageable pageable);

    // Método para buscar consultas por médico com paginação - Query derivada:
    // Spring gera SELECT * FROM consultas WHERE medico_id = ?1
    Page<Consulta> findByMedicoId(Long medicoId, Pageable pageable);

    // Método para buscar consultas por status com paginação - Query derivada:
    // Spring gera SELECT * FROM consultas WHERE status = ?1
    Page<Consulta> findByStatus(StatusConsulta status, Pageable pageable);

    /*
     * Método que verifica conflito de horário para evitar agendamento duplo do
     * mesmo médico
     * Query personalizada:
     * Verifica se existe alguma consulta para o mesmo médico no mesmo horário que
     * não esteja cancelada ou marcada como não compareceu, indicando um conflito de agendamento.
     */
    @Query("SELECT COUNT(c) > 0 FROM Consulta c " +
            "WHERE c.medico.id = :medicoId " +
            "AND c.dataHora = :dataHora " +
            "AND c.status NOT IN ('CANCELADA', 'NAO_COMPARECEU')")
    boolean existeConflito(@Param("medicoId") Long medicoId, @Param("dataHora") LocalDateTime dataHora);

    /*
     * Método que lista consultas de um dia especifico para o dashboard do médico
     * Query personalizada: Busca todas as consultas agendadas para um intervalo de
     * tempo específico e ordena por data e hora, permitindo que o médico visualize
     * suas consultas do dia de forma organizada no dashboard.
     */
    @Query("SELECT c FROM Consulta c " +
            "WHERE c.dataHora BETWEEN :inicio AND :fim " +
            "AND c.status = 'AGENDADA' ORDER BY c.dataHora ASC")
    List<Consulta> findConsultasDoDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

}
