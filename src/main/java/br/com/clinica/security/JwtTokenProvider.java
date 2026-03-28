package br.com.clinica.security;

import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration; // 86400000 = 24h

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // 604800000 = 7 dias

    /* ==== Geração de Token de Acesso ================================================== */

    /**
     * Gera o token de acesso (24 horas).
     * @param userDetails - detalhes do usuário para extrair o email (subject)
     * @return token JWT de acesso com validade de 24 horas.
     */
    public String gerarToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(), expiration);
    }

    /**
     * Gera o token de refresh (7 dias).
     * @param userDetails - detalhes do usuário para extrair o email (subject)
     * @return token JWT de refresh com validade de 7 dias.
     */
    public String gerarRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(), refreshExpiration);
    }

    /** 
     * Método auxiliar para construir o token JWT.
     * @param subject - email do usuário
     * @param exp     - tempo de expiração em milissegundos
     * @return token JWT com String compactado
     * 
     * O token é construído usando o Jwts.builder() do jjwt, onde:
     * - subject: é o email do usuário (identificador principal)
     * - issuedAt: data de geração do token
     * - expiration: data de expiração calculada a partir do tempo atual + expiração
     * - signwith: assina o token usando a chave secreta e o algoritmo HS256 
     * O resultado é um token JWT compactado em formato String, pronto para ser enviado ao cliente.
     */
    private String buildToken(String subject, long exp) {
        return Jwts.builder()
                .subject(subject)                                          
                .issuedAt(new Date())                                      
                .expiration(new Date(System.currentTimeMillis() + exp))    
                .signWith(getSecretKey())                                 
                .compact();

    }

    /* ==== Extração ================================================== */

    /**
     * Extrai o email (subject) do token JWT.
     * @param token - token JWT do qual se deseja extrair o email
     * @return email do usuário contido no campo "subject" do token JWT.
     */
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    /**
     * Extrai a data de expiração do token JWT.
     * @param token - token JWT do qual se deseja extrair a data de expiração
     * @return data de expiração do token contida no campo "expiration" do token JWT.
     */
    public Date extrairExpiracao(String token) {
        return extrairClaims(token).getExpiration();
    }

    /* ==== Validação ================================================== */

    /**
     * Valida se o token JWT pertence ao usuário e se não está expirado.
     * @param token - token JWT a ser validado 
     * @param userDetails - detalhes do usuário para comparar o email (subject) do token com o username do userDetails
     * @return true se o token for válido (email corresponde e não está expirado), false caso contrario.
     */
    public boolean isTokenValido(String token, UserDetails userDetails) {
        final String email = extrairEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    /**
     * Verifica se o token JWT está expirado comparando a data de expiração do token com a data atual.
     * @param token - token JWT a ser verificado
     * @return true se o token estiver expirado (data de expiração antes da data atual), false caso contrario.
     */
    private boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }


    /* ==== Internos ================================================== */

    /**
     * Método auxiliar para extrair os claims do token JWT. 
     * @param token - token JWT do qual se deseja extrair os claims.
     * @return claims do token JWT, que incluem informações como subject (email), issuedAt, expiration, etc.
     * 
     * O método utiliza o Jwts.parser() do jjwt para analisar o token JWT, verificando a assinatura com a chave secreta.
     * Se a assinatura for válida, ele retorna os claims contidos no token, que podem ser usados para extrair informações como o email (subject) e a data de expiração.
     */
    private Claims extrairClaims(String token) {
       return Jwts.parser()
            .verifyWith(getSecretKey())   // verifica a assinatura
            .build()
            .parseSignedClaims(token)
            .getPayload();

    }

    /**
     * Método auxiliar para obter a chave secreta usada para assinar e verificar os tokens JWT.
     * @return SecretKey gerada a partir da String secreta configurada no application.properties, 
     * decodificada de Base64 e convertida para uma chave HMAC SHA-256.
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

/**
 * Está Classe JwtTokenProvider é responsável por gerar, extrair informações e validar tokens JWT para autenticação
 * e autorização, ela é responável por toda a lógica de tokens JWT. 
 * Usa jjwt 0.12.x com algoritmo HMAC-SHA256 (HS256) para assinar os tokens.
 */
