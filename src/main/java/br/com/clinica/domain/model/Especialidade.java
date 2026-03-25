package br.com.clinica.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Especialidade medica (ex: Cardiologia, Pediatria, etc.)
 * Dados iniciais inseridos pelo Flyway - V2__insert_especialidades.sql
 * Relacionamento N:N com médico via tabela medico_especialidade (muitos médicos podem ter muitas especialidades).
 * O campo "nome" é unico para garantir que não haja especialidades duplicadas.
 */
@Entity
@Table(name = "especialidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especialidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

}
