package br.com.clinica.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 - recurso não encontrado
     * @param ex a exceção lançada quando um recurso não é encontrado
     * @return uma resposta HTTP com status 404 e uma mensagem de erro personalizada
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(404, ex.getMessage()));
    }

    /**
     * 422 - violação de regra de negócio
     * @param ex a exceção lançada quando uma regra de negócio é violada
     * @return uma resposta HTTP com status 422 e uma mensagem de erro personalizada
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(422, ex.getMessage()));
    }

    /**
     * 400 - erros de validação do @Valid (retorna campo por campo)
     * @param ex a exceção lançada quando a validação de um objeto falha
     * @return uma resposta HTTP com status 400 e um mapa de erros, onde a chave é o nome do campo e o valor é a mensagem de erro correspondente
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
            error -> {
                String campo = ((FieldError) error).getField();
                String mensagem = error.getDefaultMessage();
                erros.put(campo, mensagem);
            }
        );
        return ResponseEntity.badRequest().body(erros);
    }

    /**
     * 401 - credenciais invalidas no login
     * @param ex a exceção lançada quando as credenciais de login são inválidas
     * @return uma resposta HTTP com status 401 e uma mensagem de erro personalizada indicando que o email ou senha são inválidas
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "Email ou senha inválidos"));
    }

    /**
     * 401 - token JWT expirado
     * @param ex a exceção lançada quando um token JWT expirou
     * @return uma resposta HTTP com status 401 e uma mensagem de erro personalizada indicando que o token expirou e que o usuário deve fazer login novamente
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "Token expirado. Faca login novamente"));
    }

    /**
     * 401 - token JWT inválido (malformado, assinatura errada, etc.)
     * @param ex a exceção lançada quando um token JWT é inválido por algum motivo (malformado, assinatura errada, etc.)
     * @return uma resposta HTTP com status 401 e uma mensagem de erro perssonalizada indicando que o token é inválido
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "Token invalido"));
    }

    /**
     * 403 - acesso negado (usuário autenticado, mas sem permissão para acessar o recurso)
     * @param ex a exceção lançada quando um usuário autenticado tenta acessar um recurso para o qual ele não tem permissão
     * @return uma resposta HTTP com status 403 e uma mensagem de erro personalizada indicando que o acesso foi negado 
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(403, "Acesso negado"));
    }

    /**
     * 500 - qualquer erro interno não tratado 
     * @param ex a exceção lançada quando ocorre um erro interno no servidor que não foi tratado por nenhum dos outros métodos de tratamento de exceção
     * @return uma resposta HTTP com status 500 e uma mensagem de erro personalizada indicando que ocorreu um erro interno no servidor
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(500, "Erro interno do servidor"));
    }
}

/**
 * Está classe é responsável por tratar as exceções lançadas em toda a aplicação de forma centralizada, 
 * utilizando a anotação @RestControllerAdvice do Spring. Ela define métodos de tratamento para diferentes tipos de exceção.
*/
