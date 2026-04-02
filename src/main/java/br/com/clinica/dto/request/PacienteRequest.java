package br.com.clinica.dto.request;

import java.time.LocalDate;

import br.com.clinica.domain.enums.Sexo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PacienteRequest(

        @NotBlank(message = "Nome é obrigatório") 
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres") 
        String nome,

        @NotBlank(message = "CPF é obrigatório") 
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos númericos") 
        String cpf,

        @Email(message = "Email inválido") 
        String email,

        // @Pattern permite validar o formato do telefone
        @Pattern(regexp = "\\d{10,11}", message = "Telefone deve ter 10 ou 11 dígitos") 
        String telefone,

        // @Past garante que a data de nascimento seja no passado
        @Past(message = "Data de nascimento deve ser uma data passada") 
        LocalDate dataNascimento,

        Sexo sexo,

        /* @Valid propaga a validação para dentro do EnderecoDTO, garantindo que as regras de validação definidas lá sejam aplicadas 
        quando um endereço for fornecido.*/
        @Valid 
        EnderecoDTO endereco,

        // ID do convenio (opcional - pode ser nulo)
        Long convenioId

) {}

/**
 * DTO de entrada para criação e atualização de Paciente.
 * Usado em POST e PUT /api/v1/pacientes.
*/
