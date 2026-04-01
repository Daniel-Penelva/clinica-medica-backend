package br.com.clinica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/teste")
@Tag(name = "Teste de verificacao de autenticacao", description = "Endpoints de teste para verificar autenticacao JWT")
public class TesteController {

    @GetMapping("/ping")
    @Operation(summary = "Testando autenticacao JWT", description = "Endpoint de teste para verificar se a autenticacao JWT esta funcioando. Deve retornar 200 se o token for valido e 401 se nao for.")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("JWT funcionando! Voce está autenticado.");
    }
}

/**
 * Este é um controlador de teste simples para verificar se a autenticação JWT está funcionando corretamente.
 * 
 * Teste: 
 *      Sem token -> deve retornar 401
 *      Com token válido -> deve retornar 200 com a mensagem
 *      Com token expirado -> deve retornar 401 com mensagem do GlobalExceptionHandler
*/
