package br.com.clinica.dto.response;

import java.time.LocalDateTime;

import br.com.clinica.domain.enums.StatusConsulta;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        String nomePaciente,
        Long medicoId,
        String nomeMedico,
        String crmMedico,
        LocalDateTime dataHora,
        StatusConsulta status,
        String motivoCancelamento,
        boolean temProntuario // indica se ja tem prontuario criado

) {}

/**
 * DTO de saída para consulta.
 * Retorna nomes (não IDs) de pacientes e medico para facilitar a exibição.
 */
