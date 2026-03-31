package br.com.clinica.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.request.LoginRequest;
import br.com.clinica.dto.response.JwtResponse;
import br.com.clinica.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Login e renovacao de tokens JWT")
public class AuthController {

    private final AuthService authService;

    /**
     * Login: recebe email e senha, autentica o usuário e retorna um token JWT de acesso e refresh token.
     * Endpoint publico: não exige token JWT no header.
     * @param request - Objeto contendo email e senha do usuário a ser autenticado 
     * @return ResponseEntity contendo o token JWT de acesso, refresh token, role, email e tempo de expiração.
    */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario e gerar token JWT", description = "Autentica um usuario com email e senha e gera um token JWT de acesso e refresh token.")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.autenticar(request));
    }


    /**
     * Refresh: renova o token JWT de acesso usando um refresh token válido. O refresh token é um token de longa duração (7 dias)
     * O Angular envia o refresh token no header 'Refresh-Token' para este endpoint, que é público e não exige autenticação.
     * O authService valida o refresh token, extrai o email, busca o usuário e gera um novo toke JWT de acesso e refresh token.
     * @param refreshToken - token de atualização enviado pelo cliente para renovar o token de acesso
     * @return ResponseEntity contendo o token JWT de acesso, refresh token, role, email e tempo de expiração.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acesso", description = "Renova o token JWT de acesso usando um refresh token valido. O refresh token é um token de longa duração (7 dias) que pode ser usado para obter um novo token de acesso sem precisar reautenticar o usuario.")
    public ResponseEntity<JwtResponse> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return ResponseEntity.ok(authService.renovarToken(refreshToken));
    }
    
}

/**
 * Camada de Controle - Está classe expõe os endpoints públicos de autenticação. Esses endpoints são liberados no SecurityConfig e 
 * não precisam de token */ 
