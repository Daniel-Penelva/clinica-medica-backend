package br.com.clinica.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProntuarioRequest(

        // Obrigatório apenas no POST - ignorado no PUT
        Long consultaId,

        @NotBlank(message = "Anamnese é obrigatória") 
        String anamnese,

        @NotBlank(message = "Diagnóstico é obrigatório") 
        String diagnostico,

        String prescricao,
        String observacoes
    ) {}

/**
 * DTO para criação e atualização do prontuário.
 * consultaId é usado apenas na criação (POST).
 */
