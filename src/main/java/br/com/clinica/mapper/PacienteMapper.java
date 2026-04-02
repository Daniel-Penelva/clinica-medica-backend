package br.com.clinica.mapper;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import br.com.clinica.domain.model.Endereco;
import br.com.clinica.domain.model.Paciente;
import br.com.clinica.dto.request.EnderecoDTO;
import br.com.clinica.dto.request.PacienteRequest;
import br.com.clinica.dto.response.EnderecoResponse;
import br.com.clinica.dto.response.PacienteResponse;

@Component
public class PacienteMapper {

    /**
     * Converte um PacienteRequest em um Paciente (entidade)
     * Usado no cadastro (POST) e atualização (PUT) de pacientes
     * 
     * @param request - objeto de requisição contendo os dados do paciente a ser cadastrado
     * @return Paciente - entidade pronta para ser persistida no banco de dados
     */
    public Paciente toEntity(PacienteRequest request) {

        return Paciente.builder()
                .nome(request.nome())
                .cpf(request.cpf())
                .email(request.email())
                .telefone(request.telefone())
                .dataNascimento(request.dataNascimento())
                .sexo(request.sexo())
                .endereco(toEnderecoEntity(request.endereco()))
                .build();

        // OBS. convenioId é tratado no Service (vai precisar buscar o Convenio no banco de dados para associar ao Paciente)
    }

    /**
     * Converte um Paciente (entidade) em um PacienteResponse (DTO de resposta/saida)
     * Usado em todos os métodos que retornam dados ao frontend.
     * 
     * @param paciente - entidade do paciente a ser convertida para resposta
     * @return PacienteResponse - DTO de resposta contendo os dados do paciente formatados para o frontend
     */
    public PacienteResponse toResponse(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(),
                paciente.getEmail(),
                paciente.getTelefone(),
                paciente.getDataNascimento(),
                calcularIdade(paciente.getDataNascimento()),
                paciente.getSexo(),
                toEnderecoResponse(paciente.getEndereco()),
                paciente.getConvenio() != null ? paciente.getConvenio().getNome() : null,
                paciente.getAtivo());
    }


    /**
     * Converte Paciente (entidade) -> PacienteResponse (DTO de saída) para atualizar os dados de um paciente existente.
     * Usado no método de atualização (PUT) para refletir as mudanças feitas no paciente.
     * Usado em todos os métodos que retornam dados ao frontend, para garantir que as informações estejam sempre atualizadas.
     * 
     * Retorna um void porque a atualização é feita diretamente na entidade do paciente passada como parâmetro,
     * e o método toResponse é usado para converter a entidade atualizada em um DTO de resposta para o frontend.
     * 
     * @param request - objeto de requisição contendo os dados atualizados do paciente
     * @param paciente - entidade do paciente a ser atualizada com os novos dados do request
     */
    public void updateFromRequest(PacienteRequest request, Paciente paciente) {
        paciente.setNome(request.nome());
        paciente.setEmail(request.email());
        paciente.setTelefone(request.telefone());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setSexo(request.sexo());
        paciente.setEndereco(toEnderecoEntity(request.endereco()));
        
        // OBS. CPF NAO atualiza: é identificador único e não deve mudar
        // OBS. convenioId é tratado no Service
    }

    /* ========== Métodos auxiliares privados ========================================== */

    /**
     * Converte um EnderecoDTO em um Endereco (entidade) para ser associado ao Paciente.
     * Usado no método toEntity para criar a entidade de endereço a partir dos dados do DTO de requisição.
     * 
     * @param dto - objeto de requisição contendo os dados do endereço a ser convertido para entidade do paciente
     * @return Endereco - entidade de endereço pronta para ser associada ao paciente e persistida no banco de dados
     */
    private Endereco toEnderecoEntity(EnderecoDTO dto) {
        if (dto == null) return null;
        return Endereco.builder()
                .logradouro(dto.logradouro())
                .numero(dto.numero())
                .complemento(dto.complemento())
                .bairro(dto.bairro())
                .cidade(dto.cidade())
                .uf(dto.uf())
                .cep(dto.cep())
                .build();
    }

    /**
     * Converte um Endereco (entidade) em um EnderecoResponse (DTO de resposta/saida) para ser incluído no PacienteResponse.
     * Usado no método toResponse para formatar os dados do endereço do paciente para o frontend.
     * 
     * @param endereco - entidade de endereço do paciente a ser convertida para resposta
     * @return EnderecoResponse - DTO de resposta contendo os dados do endereço formatados para o frontend, ou null se o endereço for null
     */  
    private EnderecoResponse toEnderecoResponse(Endereco endereco) {
        if (endereco == null) return null;
        return new EnderecoResponse(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getCep()
        );
    }

    /**
     * Calcula a idade em anos a partir da data de nascimento fornecida.
     * Retorna null se a data for nula.
     * 
     * O que faz essa codificação: return Period.between(dataNascimento, LocalDate.now()).getYears();
     * Perid.between() calcula a diferença entre duas datas (dataNascimento e data atual) e retorna um objeto Period
     * que representa essa diferença em termos de anos, meses e dias. O método getYears() extrai apenas a parte dos anos
     * dessa diferença, que é a idade do paciente. Se a data de nascimento for null, o método retorna null para evitar erros.
     * 
     * @param dataNascimento - a data de nascimento do paciente, usada para calcular a idade 
     * @return Integer - a idade do paciente em anos, ou null se a data de nascimento for null
     */
    private Integer calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) return null;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
}


/**
 * Está classe é responsável por converter entre as entidades do domínio (Paciente e Endereco) e os DTOs de requisição 
 * e resposta (PacienteRequest, PacienteResponse, EnderecoDTO, EnderecoResponse).
 * 
 * Ela é usada para mapear os dados recebidos do frontend (via PacienteRequest) para as entidades que serão persistidas no banco de dados, 
 * e para formatar os dados das entidades em um formato adequado para o frontend (via PacienteResponse).
 * 
 * O Mapper e responsável por converter entre a entidade JPA e os DTOs. Ele centraliza essa lógica e evita que o Service ou Controller fiquem 
 * com código de conversão espalhado
*/