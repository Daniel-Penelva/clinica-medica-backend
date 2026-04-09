package br.com.clinica.dto.response;

import java.time.LocalDateTime;

public record ProntuarioResponse(
        Long id,
        Long consultaId,
        Long pacienteId,
        String nomePaciente,
        Long medicoId,
        String nomeMedico,
        LocalDateTime dataConsulta,
        String anamnese,
        String diagnostico,
        String prescricao,
        String observacoes,
        LocalDateTime criadoEm
) {}

/**
 * DTO de saida para Prontuario.
 */

