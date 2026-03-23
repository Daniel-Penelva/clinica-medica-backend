package br.com.clinica.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " com id " + id + " não encontrado.");
    }
    
}

/*
 * Lançada quando um recurso não é encontrado no banco de dados (404 Not Found). 
*/
