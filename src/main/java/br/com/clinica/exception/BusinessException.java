package br.com.clinica.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}

/*
 * Lançada para violações de regras de negócio (422 Unprocessable Entity).
 * Exemplo: CPF duplicado, email já cadastrado, horário conflitante, médico inativo, etc.
*/ 
