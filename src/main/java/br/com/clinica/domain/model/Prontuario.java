package br.com.clinica.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
 * Entidade que representa um prontuário eletrônico da consulta.
 */
@Entity
@Table(name = "prontuarios")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    /**
     * Relacionamento 1:1 com consulta - um prontuario está associado a uma única consulta, e cada consulta tem um prontuário associado.
     * optional = false garante que todo prontuário deve estar associado a uma consulta, ou seja, não pode existir um prontuário sem consulta.
     * FetchType.LAZY é usado para otimizar o desempenho carregando a consulta de um prontuário apenas quando necessário.
     * O campo "consulta_id" na tabela "prontuarios" é uma chave estrangeira (FK) que referencia a tabela "consultas".
    */
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", unique = true)
    private Consulta consulta;

    // TEXT: sem limite de tamanho - ideal para textos médicos longos
    @Column(columnDefinition = "TEXT")
    private String anamnese;    // histórico e queixas do paciente

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String prescricao;  // medicamentos receitados

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

}
