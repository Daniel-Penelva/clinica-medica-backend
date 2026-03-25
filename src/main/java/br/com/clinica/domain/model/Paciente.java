package br.com.clinica.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import br.com.clinica.domain.enums.Sexo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa um paciente da clinica médica.
 * O campo "cpf" é único para garantir que não haja pacientes duplicados e é usado como identificador principal do paciente - nullable = false para garantir que todo paciente tenha um CPF válido.
 * O campo "email" é único para garantir que não haja pacientes duplicados e é usado para contato e comunicação com o paciente.
 * Relacionamentos:
 * - N:1 com convênio (muitos pacientes podem ter um mesmo convênio, mas cada paciente tem no máximo um convênio).
 * - 1:N com consulta (um paciente pode ter muitas consultas, mas cada consulta tem apenas um paciente).
 * - Embedded com endereço para simplificar o modelo e evitar a necessidade de uma tabela separada de endereços.
 */
@Entity
@Table(name = "pacientes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 11)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)  // Armazena o sexo como string no banco (MASCULINO, FEMININO) para melhor legibilidade e manutenção.
    @Column(length = 20)
    private Sexo sexo;

    // Campos de endereço embutidos na tabela 'pacientes' para simplificar o modelo e evitar a necessidade de uma tabela separada de endereços.
    @Embedded
    private Endereco endereco;

    /**
     * Relacionamento N:1 com convênio - muitos pacientes podem ter um mesmo convênio, mas cada paciente tem no máximo um convênio.
     * O campo "convenio_id" na tabela "pacientes" é uma chave estrangeira que referencia a tabela "convenios".
     * FK convenio_id na tabela "pacientes" é nullable para permitir pacientes sem convênio (convênio é opcional).
     * FetchType.LAZY é usado para otimizar o carregamento dos dados, carregando o convênio apenas quando necessário.
.    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id")
    private Convenio convenio;

    @Builder.Default
    private Boolean ativo = true;  // Por padrão, um novo paciente é criado como ativo. O campo "ativo" pode ser atualizado para false para marcar o paciente como inativo sem deletar seu registro.

    // Preenchido automaticamente pelo @EnableJpaAuditing
    @CreatedDate  // Preenche automaticamente a data e hora de criação do registro.
    @Column(name = "criado_em", updatable = false)  // updatable = false para garantir que a data de criação não seja alterada após a inserção (é imutável).
    private LocalDateTime criadoEm;

    @LastModifiedDate  // Preenche automaticamente a data e hora da última atualização do registro.
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;  // registro da última atualização do paciente, atualizando automaticamente sempre que o paciente for modificado.

    /**
     * Relacionamento 1:N com consulta - um paciente pode ter muitas consultas, mas cada consulta tem apenas um paciente.
     * O campo "paciente_id" na tabela "consultas" é uma chave estrangeira que referencia a tabela "pacientes".
     * O cascade All garante que ao deletar um paciente, todas as suas consultas associadas sejam deletadas automaticamente.
     * orphanRemoval: remove consultas sem paciente automaticamente para manter a integridade dos daddos.
     * FetchType.LAZY é usado para otimizar o carregamento dos dados, carregando as consultas apenas quando necessário.
    */
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Consulta> consultas = new ArrayList<>(); // Lista de consultas associados ao paciente

}
