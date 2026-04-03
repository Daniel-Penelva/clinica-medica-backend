package br.com.clinica.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.clinica.domain.model.Especialidade;
import br.com.clinica.domain.model.Medico;
import br.com.clinica.dto.request.MedicoRequest;
import br.com.clinica.dto.response.EspecialidadeResponse;
import br.com.clinica.dto.response.MedicoResponse;

@Component
public class MedicoMapper {

    /**
     * Converte um MedicoRequest em um Medico (entidade)
     * Usado no cadastro (POST) e atualização (PUT) de medicos
     * As especialidades NÃO são mapeadas aqui - o Service as busca no banco pelos
     * IDs e vincula depois.
     * 
     * @param request - objeto de requisição contendo os dados do medico a ser
     *                cadastrado
     * @return Medico - entidade pronta para ser persistida no banco de dados
     */
    public Medico toEntity(MedicoRequest request) {
        return Medico.builder()
                .nome(request.nome())
                .crm(request.crm())
                .email(request.email())
                .telefone(request.telefone())
                .build();

        // especialidades vinculadas pelo Service após busca no banco de dados
    }

    /**
     * Converte um Medico (entidade) em um MedicoResponse (DTO de resposta/saida)
     * Mapeia a lista de Especialidade para lista de EspecialidadeResponse.
     * Usado em todos os métodos que retornam dados ao frontend.
     * 
     * @param medico - entidade do medico a ser convertida para resposta
     * @return MedicoResponse - DTO de resposta contendo os dados do medico formatados para o frontend
     */
    public MedicoResponse toResponse(Medico medico) {
        return new MedicoResponse(
                medico.getId(), 
                medico.getNome(), 
                medico.getCrm(), 
                medico.getEmail(),
                medico.getTelefone(), 
                medico.getAtivo(), 
                toEspecialidadeResponseList(medico.getEspecialidades()));
    }


    /**
     * Atualiza os campos de um Medico existente a partir do Request.
     * Usado no método de atualização (PUT) para refletir as mudanças feitas no medico - nao cria objeto novo, atualiza o existente.
     * Usado em todos os métodos que retornam dados ao frontend, para garantir que as informações estejam sempre atualizadas.
     * As especialidades sao gerenciadas separadamente pelo Service.
     * 
     * Retorna um void porque a atualização é feita diretamente na entidade do medico passada como parâmetro,
     * e o método toResponse é usado para converter a entidade atualizada em um DTO de resposta para o frontend.
     * 
     * @param request - objeto de requisição contendo os dados atualizados do medico
     * @param paciente - entidade do medico a ser atualizada com os novos dados do request
     */
    public void updateFromRequest(MedicoRequest request, Medico medico) {
        medico.setNome(request.nome());
        medico.setEmail(request.email());
        medico.setTelefone(request.telefone());
        
        // CRM não é atualizado: é identificador único e imutável
    }

    /* ========== Métodos auxiliares privados ========================================== */

    /**
     * Converte uma lista de entidades Especialidade em uma lista de DTOs EspecialidadeResponse.
     * Trata casos de lista nula ou vazia retornando lista vazia.
     * 
     * @param especialidades lista de especialidades do domínio a serem convertidas
     * @return lista de EspecialidadeResponse ou Collections.emptyList() se entrada for nula/vazia
     */
    private List<EspecialidadeResponse> toEspecialidadeResponseList(List<Especialidade> especialidades) {

        // Verifica se a lista de especialidades é nula ou vazia para evitar NullPointerException no stream
        if (especialidades == null || especialidades.isEmpty()) {
            return Collections.emptyList();
        }

        // Converte cada Especialidade em EspecialidadeResponse usando Stream API e mapeia apenas ID e nome
        return especialidades.stream().map(
            e -> new EspecialidadeResponse(e.getId(), e.getNome())).toList();
    }
}

/**
 * Está classe é responsável por converter entre as entidades do domínio (Medico e Especialidade) e os DTOs de requisição 
 * e resposta (MedicoRequest, MedicoResponse, EspecialidadeResponse).
 * 
 * Ela é usada para mapear os dados recebidos do frontend (via MedicoRequest) para as entidades que serão persistidas no banco de dados, 
 * e para formatar os dados das entidades em um formato adequado para o frontend (via MedicoResponse).
 * 
 * O Mapper e responsável por converter entre a entidade JPA e os DTOs. Ele centraliza essa lógica e evita que o Service ou Controller fiquem 
 * com código de conversão espalhado
*/
