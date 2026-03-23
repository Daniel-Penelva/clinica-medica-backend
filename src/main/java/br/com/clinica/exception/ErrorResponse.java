package br.com.clinica.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp) {

    // Construtor secundário para facilitar a criação de respostas de erro sem precisar fornecer o timestamp (gera automatico)
    ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}

/*
 * Rcord para padronizar as respostas de erro da API. Contém o status HTTP, a mensagem de erro e o timestamp do erro.
*/ 