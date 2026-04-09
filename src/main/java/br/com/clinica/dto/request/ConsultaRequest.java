package br.com.clinica.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(

        @NotNull(message = "Paciente é obrigatório") 
        Long pacienteId,

        @NotNull(message = "Medico é obrigatório") 
        Long medicoId,

        @NotNull(message = "Data e hora são obrigatórias") 
        @Future(message = "A consulta deve ser agendada para uma data futura") 
        LocalDateTime dataHora
    ) {}

/**
 * DTO para agendamento de uma nova consulta.
 */