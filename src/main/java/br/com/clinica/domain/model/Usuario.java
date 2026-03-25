package br.com.clinica.domain.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.clinica.domain.enums.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa um usuário do sistema, que pode ser um ADMIN, MEDICO ou RECEPCIONISTA.
 * Entidade de autenticação do sistema, implementa UserDetails para integração com Spring Security + JWT.
 * Pode está vinculada a um Médico (apenas se for do tipo MEDICO) para controle de acesso por perfil.
 * O campo "email" é único para garantir que não haja usuários duplicados e é usado como nome de usuário para autenticação.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    // Sempre armazenado como hash BCrypt - NUNCA em texto puro!
    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)  // Armazena o tipo do usuário como string no banco de dados, facilitando a leitura e manutenção.
    @Column(nullable = false, length = 20)
    private TipoUsuario role; // ADMIN, MEDICO, RECEPCIONISTA


    /** 
     * Relacionamento 1:1 com médico - apenas usuários do tipo MEDICO terão um médico associado.
     * Um usuário do tipo ADMIN ou RECEPCIONISTA terá este campo nulo. O médico associado a um usuário do tipo MEDICO é carregado sob demanda (fetch LAZY) para otimizar o desempenho.
     * O fetch é LAZY para evitar carregar o médico desnecessariamente quando buscamos um usuário que não é médico 
     * **/
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    // Por padrão, um novo usuário é criado como ativo (pode fazer login). O campo "ativo" pode ser atualizado para false para bloquear o acesso do usuário sem deletar sua conta. 
    @Builder.Default 
    private Boolean ativo = true; 

    
    // ---- Implementação UserDetails para integração com Spring Security ----
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // ROLE_ADMIN, ROLE_MEDICO ou ROLE_RECEPCIONISTA
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;  // O campo "senha" armazena o hash da senha do usuário, que é retornado para autenticação pelo Spring Security.
    }

    @Override
    public String getUsername() {
        return email;  // O email é usado como nome de usuário para autenticação - identificador único do usuário.
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Não implementando expiração de conta
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Não implementando bloqueio de conta
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Não implementando expiração de credenciais
    }

    @Override
    public boolean isEnabled() {
        return ativo; // O usuário é habilitado para login se estiver ativo
    }
    
}
