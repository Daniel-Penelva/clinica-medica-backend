package br.com.clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelamentoRequest(

        @NotBlank(message = "Motivo do cancelamento é obrigatório")
        @Size(min = 5, max = 255, message = "Motivo deve ter entre 5 e 255 caracteres") 
        String motivo
    ) {}

/**
 * DTO para cancelamento de uma consulta.
 * O motivo é obrigatório para rastreabilidade
 */
