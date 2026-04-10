package br.com.clinica.mapper;

import org.springframework.stereotype.Component;

import br.com.clinica.domain.enums.StatusConsulta;
import br.com.clinica.domain.model.Consulta;
import br.com.clinica.domain.model.Medico;
import br.com.clinica.domain.model.Paciente;
import br.com.clinica.dto.request.ConsultaRequest;
import br.com.clinica.dto.response.ConsultaResponse;

@Component
public class ConsultaMapper {

    /**
     * Cria uma entidade Consulta a partir do request e das entidades (Paciente e Medico) buscadas.
     * Paciente e Medico já foram validados e buscados no Service.
     * 
     * @param request - objeto de requisição contendo os dados da consulta a ser cadastrado
     * @param paciente - a entidade do paciente contendo os dados a serem buscadas
     * @param medico - a entidade do medico contendo os dados a serem buscadas
     * @return Consulta - entidade pronta para ser persistida no banco de dados
     */
    public Consulta toEntity(ConsultaRequest request, Paciente paciente, Medico medico) {
        return Consulta.builder()
                .paciente(paciente)
                .medico(medico)
                .dataHora(request.dataHora())
                .status(StatusConsulta.AGENDADA)
                .build();
    }

    /**
     * Converte Consulta (entidade) em um ConsultaResponse (DTO de resposta/saida)
     * Inclui nome de paciente e medico para exibição direta no frontend
     * 
     * @param consulta - entidade da consulta a ser convertida para resposta
     * @return ConsultaResponse - DTO de resposta contendo os dados da consulta formatados para o frontend
     */
    public ConsultaResponse toResponse(Consulta consulta) {
        return new ConsultaResponse(
            consulta.getId(),
            consulta.getPaciente().getId(),
            consulta.getPaciente().getNome(),
            consulta.getMedico().getId(),
            consulta.getMedico().getNome(),
            consulta.getMedico().getCrm(),
            consulta.getDataHora(),
            consulta.getStatus(),
            consulta.getMotivoCancelamento(),
            consulta.getProntuario() != null
        );
    }
}

/**
 * Está classe é usada para mapear os dados recebidos do frontend (via ConsultaRequest) para as entidades que serão persistidas no banco de dados, 
 * e para formatar os dados das entidades em um formato adequado para o frontend (via ConsultaResponse).
 * 
 * O Mapper e responsável por converter entre a entidade JPA e os DTOs. Ele centraliza essa lógica e evita que o Service ou Controller fiquem 
 * com código de conversão espalhado
*/