package br.com.clinica.dto.response;

import java.time.LocalDateTime;

import br.com.clinica.domain.enums.StatusConsulta;

public record ConsultaHojeResponse(
        Long id,
        LocalDateTime dataHora,
        String nomePaciente,
        String nomeMedico,
        String especialidade, // primeira especialidade do medico
        StatusConsulta status

) {}

/**
 * Consulta simplificada para exibição na agenda do dia.
 */
