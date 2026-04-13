package br.com.clinica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.domain.enums.StatusConsulta;
import br.com.clinica.domain.model.Consulta;
import br.com.clinica.domain.model.Medico;
import br.com.clinica.domain.model.Paciente;
import br.com.clinica.dto.request.CancelamentoRequest;
import br.com.clinica.dto.request.ConsultaRequest;
import br.com.clinica.dto.response.ConsultaResponse;
import br.com.clinica.exception.BusinessException;
import br.com.clinica.exception.ResourceNotFoundException;
import br.com.clinica.mapper.ConsultaMapper;
import br.com.clinica.repository.ConsultaRepository;
import br.com.clinica.repository.MedicoRepository;
import br.com.clinica.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaMapper consultaMapper;

    // --- AGENDAR ----------------------------------------------------

    /**
     * Agenda uma nova consulta validando regras de negócio.
     * 
     * <p>Validações realizadas:</p>
     * <ul>
     * <li>Paciente e médico existem e estão ativos</li>
     * <li>Médico não tem conflito de horário</li>
     * </ul>
     * 
     * @param request Dados da consulta a ser agendada
     * @return        Consulta agendada com ID gerado
     * @throws ResourceNotFoundException se paciente ou médico não encontrado
     * @throws BusinessException se validações de negócio falharem
     */
    @Transactional
    public ConsultaResponse agendar(ConsultaRequest request) {

        // Busca e valida o paciente
        Paciente paciente = pacienteRepository.findById(request.pacienteId()).orElseThrow(
            () -> new ResourceNotFoundException("Paciente", request.pacienteId()));

        // Regra 1: paciente deve estar ativo
        if (!paciente.getAtivo()) {
            throw new BusinessException("Não é possível agendar consulta para paciente inativo");
        }

        // Busca e válida o médico
        Medico medico = medicoRepository.findById(request.medicoId()).orElseThrow(
            () -> new ResourceNotFoundException("Medico", request.medicoId()));

        // Regra 2: medico deve estar ativo
        if (!medico.getAtivo()) {
            throw new BusinessException("Não é possível agendar consulta com medico inativo");
        }

        // Regra 3: verificar conflito de horário do médico
        if (consultaRepository.existeConflito(request.medicoId(), request.dataHora())) {
            throw new ResourceNotFoundException("Medico já possui consulta agendada neste horario");
        }

        // Cria e salva a consulta
        Consulta consulta = consultaMapper.toEntity(request, paciente, medico);
        return consultaMapper.toResponse(consultaRepository.save(consulta));
    }

    // --- LISTAR CONSULTA ----------------------------------------------------

    /**
     * Lista todas as consultas com paginação.
     * 
     * @param pageable Configurações de paginação e ordenação
     * @return         Página de consultas
     */
    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listar(Pageable pageable) {
        return consultaRepository.findAll(pageable).map(consultaMapper::toResponse);
    }

    // --- BUSCAR POR ID ----------------------------------------------------

    /**
     * Busca consulta específica por ID.
     * 
     * @param id ID da consulta
     * @return   Consulta encontrada
     * @throws ResourceNotFoundException se consulta não existir
     */
    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(Long id) {
        return consultaMapper.toResponse(consultaRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Consulta", id)));
    }

    // --- LISTAR POR PACIENTE ----------------------------------------------------
    
    /**
     * Lista consultas de um paciente específico com paginação.
     * 
     * @param pacienteId ID do paciente
     * @param pageable   Configurações de paginação
     * @return           Página de consultas do paciente
     */
    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listarPorPaciente(Long pacienteId, Pageable pageable) {
        return consultaRepository.findByPacienteId(pacienteId, pageable).map(consultaMapper::toResponse);
    }

    // --- LISTAR POR MEDICO ----------------------------------------------------

    /**
     * Lista consultas de um médico específico com paginação.
     * 
     * @param medicoId ID do médico
     * @param pageable Configurações de paginação
     * @return         Página de consultas do médico
     */
    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listarPorMedico(Long medicoId, Pageable pageable) {
        return consultaRepository.findByMedicoId(medicoId, pageable).map(consultaMapper::toResponse);
    }

    // --- TRANSIÇÕES DE STATUS ----------------------------------------------------

    /**
     * Confirma uma consulta agendada.
     * 
     * <p>Apenas consultas com status AGENDADA podem ser confirmadas.</p>
     * 
     * @param id ID da consulta
     * @return   Consulta confirmada
     * @throws BusinessException se status não permitir confirmação
     */
    @Transactional
    public ConsultaResponse confirmar(Long id) {

        Consulta consulta = buscarConsulta(id);

        if (consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new BusinessException("Somente consultas AGENDADAS podem ser confirmadas");
        }

        consulta.setStatus(StatusConsulta.CONFIRMADA);
        return consultaMapper.toResponse(consultaRepository.save(consulta));
    }

    /**
     * Cancela uma consulta com motivo.
     * 
     * <p>Não permite cancelar consultas já REALIZADAS ou CANCELADAS.</p>
     * 
     * @param id      ID da consulta
     * @param request Dados do cancelamento (motivo)
     * @return        Consulta cancelada
     * @throws BusinessException se status não permitir cancelamento
     */
    @Transactional
    public ConsultaResponse cancelar(Long id, CancelamentoRequest request) {

        Consulta consulta = buscarConsulta(id);

        if (consulta.getStatus() == StatusConsulta.REALIZADA || consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Consulta com status " + consulta.getStatus() + " nao pode ser cancelada");
        }

        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setMotivoCancelamento(request.motivo());
        return consultaMapper.toResponse(consultaRepository.save(consulta));
    }

    /**
     * Marca consulta como realizada.
     * 
     * <p>Apenas consultas AGENDADAS ou CONFIRMADAS podem ser realizadas.</p>
     * 
     * @param id ID da consulta
     * @return   Consulta realizada
     * @throws BusinessException se status não permitir realização
     */
    @Transactional
    public ConsultaResponse realizar(Long id) {

        Consulta consulta = buscarConsulta(id);

        if (consulta.getStatus() != StatusConsulta.CONFIRMADA && consulta.getStatus() != StatusConsulta.AGENDADA) {
            throw new BusinessException("Somente consultas AGENDADAS ou CONFIRMADAS podem ser realizadas");
        }

        consulta.setStatus(StatusConsulta.REALIZADA);
        return consultaMapper.toResponse(consultaRepository.save(consulta));
    }

    /**
     * Registra não comparecimento do paciente.
     * 
     * <p>Apenas consultas AGENDADAS ou CONFIRMADAS podem ter não comparecimento registrado.</p>
     * 
     * @param id ID da consulta
     * @return   Consulta com status NÃO COMPARECEU
     * @throws BusinessException se status não permitir
     */
     @Transactional
    public ConsultaResponse registrarNaoCompareceu(Long id) { 

        Consulta consulta = buscarConsulta(id);

        if (consulta.getStatus() != StatusConsulta.AGENDADA && consulta.getStatus() != StatusConsulta.CONFIRMADA) {
            throw new BusinessException("Status invalido para registrar nao comparecimento");
        }

        consulta.setStatus(StatusConsulta.NAO_COMPARECEU);
        return consultaMapper.toResponse(consultaRepository.save(consulta));
    }


    // --- MÉTODO AUXILIAR ----------------------------------------------------

    /**
     * Busca consulta por ID com validação de existência.
     * 
     * <p>Método auxiliar privado reutilizado nas transições de status.</p>
     * 
     * @param id ID da consulta
     * @return   Consulta encontrada
     * @throws ResourceNotFoundException se consulta não existir
     */
    private Consulta buscarConsulta(Long id) {
        return consultaRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Consulta", id));
    }
}
