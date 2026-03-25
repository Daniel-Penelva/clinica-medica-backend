package br.com.clinica.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import br.com.clinica.domain.enums.StatusConsulta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa uma consulta médica agendada na clínica médica
 * Relacionamentos:
 * - N:1 com paciente (muitas consultas podem estar associadas a um mesmo paciente, mas cada consulta tem apenas um paciente).
 * - N:1 com medico (muitas consultas podem estar associados a um mesmo médico, mas cada consulta tem apenas um médico).
 * - 1:1 com prontuario (cada consulta tem um prontuario associado, e cada prontuario está associado a uma única consulta).
 */
@Entity
@Table(name = "consultas")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relacionamento N:1 com paciente - muitas consultas podem estar associadas a um mesmo paciente, mas cada consulta tem apenas um paciente.
     * optional = false garante que toda consulta deve estar associada a um paciente, ou seja, não pode existir uma consulta sem paciente.
     * FetchType.LAZY é usado para otimizar o desempenho carregando o paciente de uma consulta apenas quando necessário.
     * O campo "paciente_id" na tabela "consultas" é uma chave estrangeira que referencia a tabela "pacientes".
    */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    /**
     * Relacionamento N:1 com medico - muitas consultas podem estar associadas a um mesmo médico, mas cada consulta tem apenas um médico.
    */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)  // O status da consulta é armazenado como string no banco de dados, facilitando a leitura e manutenção dos dados.
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusConsulta status = StatusConsulta.AGENDADA;  // O status da consulta, com valor padrão "AGENDADA".

    // Preenchido apenas quando cancelada
    @Column(name = "motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    @CreatedDate  // A anotação @CreatedDate indica que este campo deve ser preenchido automaticamente com a data e horário de criação da consulta.
    @Column(name = "criado_em", updatable = false)  // updatable = false para garantir que a data de criação não seja alterada após a inserção (é imutável).
    private LocalDateTime criadoEm;


    /**
     * Relacionamento 1:1 com prontuario - cada consulta tem um prontuario associado, e cada prontuario está associado a uma única consulta.
     * mappedBy = "consulta" indica que a entidade Prontuaria é a dona do relacionamento e que a chave estrangeira está na tabela "prontuarios".
     * cascade = CascadeType.ALL garante que todas a operação deletada na consulta seja propagada para o prontuario associado.
    */
    @OneToOne(mappedBy = "consulta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Prontuario prontuario;

}
