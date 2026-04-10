package br.com.clinica.mapper;

import org.springframework.stereotype.Component;

import br.com.clinica.domain.model.Consulta;
import br.com.clinica.domain.model.Prontuario;
import br.com.clinica.dto.request.ProntuarioRequest;
import br.com.clinica.dto.response.ProntuarioResponse;

@Component
public class ProntuarioMapper {

    /**
     * Cria uma entidade Prontuario a partir do request e da entidade Consulta
     * buscada.
     * 
     * @param request - objeto de requisição contendo os dados do prontuario a ser cadastrado
     * @param consulta - a entidade da consulta contendo os dados a serem buscadas
     * @return Prontuario - entidade pronta para ser persistida no banco de dados
     */
    public Prontuario toEntity(ProntuarioRequest request, Consulta consulta) {
        return Prontuario.builder()
                .consulta(consulta)
                .anamnese(request.anamnese())
                .diagnostico(request.diagnostico())
                .prescricao(request.prescricao())
                .observacoes(request.observacoes())
                .build();
    }

    /**
     * Converte um Prontuario (entidade) em um ProntuarioResponse (DTO de resposta/saida)
     * Mapeia a lista de Prontuario para lista de ProntuarioResponse.
     * Usado em todos os métodos que retornam dados ao frontend.
     * 
     * @param prontuario - entidade do prontuario a ser convertida para resposta
     * @return ProntuarioResponse - DTO de resposta contendo os dados do prontuario formatados para o frontend
     */
    public ProntuarioResponse toResponse(Prontuario prontuario) {
        return new ProntuarioResponse(
                prontuario.getId(),
                prontuario.getConsulta().getId(),
                prontuario.getConsulta().getPaciente().getId(),
                prontuario.getConsulta().getPaciente().getNome(),
                prontuario.getConsulta().getMedico().getId(),
                prontuario.getConsulta().getMedico().getNome(),
                prontuario.getConsulta().getDataHora(),
                prontuario.getAnamnese(),
                prontuario.getDiagnostico(),
                prontuario.getPrescricao(),
                prontuario.getObservacoes(),
                prontuario.getCriadoEm());
    }

    /**
     * Atualiza os campos de um Prontuario existente a partir do Request.
     * Usado no método de atualização (PUT) para refletir as mudanças feitas no prontuario - nao cria objeto novo, atualiza o existente.
     * Usado em todos os métodos que retornam dados ao frontend, para garantir que as informações estejam sempre atualizadas.
     * 
     * Retorna um void porque a atualização é feita diretamente na entidade do prontuario passado como parâmetro,
     * e o método toResponse é usado para converter a entidade atualizada em um DTO de resposta para o frontend.
     * 
     * @param request - objeto de requisição contendo os dados atualizados do prontuario
     * @param p - entidade do medico a ser atualizada com os novos dados do request
     */
    public void updateFromRequest(ProntuarioRequest request, Prontuario p) {
        p.setAnamnese(request.anamnese());
        p.setDiagnostico(request.diagnostico());
        p.setPrescricao(request.prescricao());
        p.setObservacoes(request.observacoes());
    }
}
