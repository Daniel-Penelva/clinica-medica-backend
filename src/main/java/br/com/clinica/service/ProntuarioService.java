package br.com.clinica.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.domain.enums.StatusConsulta;
import br.com.clinica.domain.model.Consulta;
import br.com.clinica.domain.model.Prontuario;
import br.com.clinica.dto.request.ProntuarioRequest;
import br.com.clinica.dto.response.ProntuarioResponse;
import br.com.clinica.exception.BusinessException;
import br.com.clinica.exception.ResourceNotFoundException;
import br.com.clinica.mapper.ProntuarioMapper;
import br.com.clinica.repository.ConsultaRepository;
import br.com.clinica.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final ConsultaRepository consultaRepository;
    private final ProntuarioMapper prontuarioMapper;

    // --- CRIAR PRONTUARIO ----------------------------------------------------

    /**
     * Cria um novo prontuário para consulta realizada.
     * 
     * <p>Validações realizadas:</p>
     * <ul>
     * <li>Consulta existe e tem status REALIZADA</li>
     * <li>Consulta ainda não possui prontuário (relação 1:1)</li>
     * </ul>
     * 
     * @param request Dados do prontuário a ser criado
     * @return        Prontuário criado com ID gerado
     * @throws ResourceNotFoundException se consulta não encontrada
     * @throws BusinessException se validações de negócio falharem
     */
    @Transactional
    public ProntuarioResponse criar(ProntuarioRequest request) {

        // Buscar a consulta
        Consulta consulta = consultaRepository.findById(request.consultaId()).orElseThrow(
                () -> new ResourceNotFoundException("Consulta", request.consultaId()));

        // Regra 1: Consulta deve ter status REALIZADA
        if (consulta.getStatus() != StatusConsulta.REALIZADA) {
            throw new BusinessException("Prontuario só pode ser criado consultas REALIZADAS");
        }

        // Regra 2: Consulta ainda não pode ter prontuário (1:1)
        if (consulta.getProntuario() != null) {
            throw new BusinessException("Está consulta já possui prontuário");
        }

        Prontuario prontuario = prontuarioMapper.toEntity(request, consulta);
        return prontuarioMapper.toResponse(prontuarioRepository.save(prontuario));
    }

    // --- BUSCAR PRONTUARIO ----------------------------------------------------

    /**
     * Busca prontuário pela ID da consulta associada (relação 1:1).
     * 
     * @param consultaId ID da consulta
     * @return           Prontuário da consulta
     * @throws ResourceNotFoundException se prontuário não existir para a consulta
     */
    @Transactional(readOnly = true)
    public ProntuarioResponse buscarPorConsulta(Long consultaId) {
        return prontuarioRepository.findByConsultaId(consultaId).map(prontuarioMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário para a consulta", consultaId));
    }

    // --- ATUALIZAR PRONTUARIO ----------------------------------------------------

    /**
     * Atualiza dados do prontuário existente.
     * 
     * <p>Autorização do médico responsável é verificada no Controller.</p>
     * 
     * @param id      ID do prontuário
     * @param request Dados atualizados do prontuário
     * @return        Prontuário atualizado
     * @throws ResourceNotFoundException se prontuário não existir
     */
    @Transactional
    public ProntuarioResponse atualizar(Long id, ProntuarioRequest request) {

        Prontuario prontuario = prontuarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Prontuario", id));

        prontuarioMapper.updateFromRequest(request, prontuario);
        return prontuarioMapper.toResponse(prontuarioRepository.save(prontuario));
    }

}
