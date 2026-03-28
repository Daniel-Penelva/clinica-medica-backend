package br.com.clinica.dto.response;

public record JwtResponse(
    String token,           // token de acesso (validade: 24h)
    String refreshToken,    // token para renovação (validade: 7 dias)
    String tipo,            // sempre 'Bearer'
    String role,            // ADMIN, MEDICO ou RECEPCIONISTA
    String email,           // email do usuario logado
    long expiresIn          // expiração em milissegundos
) {

    // Construtor de conveniência para definir o tipo como 'Bearer' por padrão.
    public JwtResponse(String token, String refreshToken, String role, String email, long expiresIn) {
        this(token, refreshToken, "Bearer", role, email, expiresIn);
    }
    
}

/* Resposta do endpoint de login - é o DTO de saída - ele só carrega dados de volta para o Angular .
 * Contém o token JWT e informações do usuário autenticado. */
