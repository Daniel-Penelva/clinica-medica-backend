package br.com.clinica.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Entidade simples. Um paciente pode opcionalmente estar vinculado a um convênio (manyToOne). 
 * O convênio tem um nome, um registro único e um status de ativo/inativo. O campo "registro" é único para garantir que não haja convênios duplicados.
 * 
 * A anotação @EntityListeners(AuditingEntityLisntener.class) é usada para habilitar o suporte à auditoria no JPA, 
 * permitindo que campos como "criadoEm" sejam automaticamente preenchidos com a data e horário de criação do registro. 
 * O campo "criadoEm" é anotado com @CreatedDate para indicar que deve ser preenchido automaticamente quando o registro for criado.
 * 
*/
@Entity
@Table(name = "convenios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Convenio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, length = 20)
    private String registro;

    @Builder.Default
    private Boolean ativo = true;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
    
}
