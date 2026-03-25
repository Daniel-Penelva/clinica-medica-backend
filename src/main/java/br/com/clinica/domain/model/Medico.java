package br.com.clinica.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa um médico da clinica médica.
 * O campo "crm" é único para garantir que não haja médicos duplicados e é usado como identificador principal do médico - nullable = false para garantir que todo médico tenha um CRM válido.
 * O campo "email" é único para garantir que não haja médicos duplicados e é usado para contato e comunicação com o médico.
 * Relacionamentos:
 * - N:N com especialidade via tabela medico_especialidade (muitos médicos podem ter muitas especialidades).
 * - 1:N com consulta (um médico pode ter muitas consultas, mas cada consulta tem apenas um médico).
 */
@Entity
@Table(name = "medicos")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 7)
    private String crm;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 11)
    private String telefone;

    @Builder.Default  // Por padrão, um médico é ativo quando criado, a menos que seja especificado o contrário.
    private Boolean ativo = true;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    /**
     * Relacionamento N:N com especialidade via tabela medico_especialidade (muitos médicos podem ter muitas especialidades).
     * @JoinTable define a tabela intermediária "medico_especialidade" com as chaves estrangeiras "medico_id" e "especialidade_id".
     * FetchType.Lazy é usado para otimizar o desempenho, carregando as especialidades de um médico apenas quando necessário.
     * @Builder.Default é usado para inicializar a lsita de especialidades como vazia por padrão, evitando NullPointerException ao adicionar especialidades a um médico recém-criado.
    */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "medico_especialidade",
        joinColumns = @JoinColumn(name = "medico_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidade_id")
    )
    @Builder.Default
    private List<Especialidade> especialidades = new ArrayList<>();

    /**
     * Relacionamento 1:N com consulta - um médico pode ter muitas consultas, mas cada consulta tem apenas um médico.
     * FetchType.LAZY é usado para otimizar o desempenho, carregando as consultas de um médico apenas quando necessário.
     * mappedBy = "medico" indica que a entidade consulta é a dona do relacionamento e que a chave estrangeira está na tabela de consultas.
     * @Builder.Default por padrão, a lista de consultas é inicializada como vazia para evitar NullPointeException ao adicionar consultas a um médico recém-criado.
    */
    @OneToMany(mappedBy = "medico", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Consulta> consultas = new ArrayList<>();

}
