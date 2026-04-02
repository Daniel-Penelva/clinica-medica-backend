package br.com.clinica.dto.response;

import java.time.LocalDate;

import br.com.clinica.domain.enums.Sexo;

public record PacienteResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        String telefone,
        LocalDate dataNascimento,
        Integer idade, // calculado no Mapper a partir de dataNascimento
        Sexo sexo,
        EnderecoResponse endereco,
        String convenio, // nome do convenio (nao o ID)
        Boolean ativo

) {}

/**
 * DTO de saída para respostas da API relacionadas a Paciente.
 * Retornando em todos os endpoints GET e nas respostas de POST/PUT.
 * Inclui 'idade' como campo calculado (não existe na entidade, mas é útil para o cliente).
*/