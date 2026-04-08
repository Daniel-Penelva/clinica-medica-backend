package br.com.clinica.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.domain.model.Especialidade;
import br.com.clinica.domain.model.Medico;
import br.com.clinica.dto.request.MedicoRequest;
import br.com.clinica.dto.response.EspecialidadeResponse;
import br.com.clinica.dto.response.MedicoResponse;
import br.com.clinica.exception.BusinessException;
import br.com.clinica.exception.ResourceNotFoundException;
import br.com.clinica.mapper.MedicoMapper;
import br.com.clinica.repository.EspecialidadeRepository;
import br.com.clinica.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final MedicoMapper medicoMapper;

    // --- CADASTRAR ----------------------------------------------------

    /**
     * Cadastra novo médico com validações de CRM/email únicos e ao menos 1 especialidade.
     * Busca especialidades por ID e associa à entidade.
     * 
     * @param request dados do médico (nome, crm, email, telefone, especialidadeIds)
     * @return MedicoResponse do médico criado
     */

    @Transactional
    public MedicoResponse cadastrar(MedicoRequest request) {

        // Regra 1: verificar se o CRM único existe
        if (medicoRepository.existsByCrm(request.crm())) {
            throw new BusinessException("CRM já cadastrado no sistema");
        }

        // Regra 2: Email único quando informado
        if (request.email() != null && !request.email().isBlank() && medicoRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado no sistema");
        }

        // Regra 3: Deve ter ao menos uma especialidade
        if (request.especialidadeIds() == null || request.especialidadeIds().isEmpty()) {
            throw new BusinessException("Médico deve ter ao menos uma especialidade");
        }

        // Converte DTO para entidade
        Medico medico = medicoMapper.toEntity(request);

        // Busca as especialidades no banco pelos IDs informados
        List<Especialidade> especialidades = especialidadeRepository.findAllByIdIn(request.especialidadeIds());

        /* Valida se todos os IDs informados existem:
         * especialidades.size() - Conta quantos objetos (especialidades) foram encontrados no BD
         * request.especialidadeIds().size() - Conta quantos IDs de especialidade o usuário enviou na requisição
         * != (comparação): O if verifica se a quantidade de especialidades encontradas é diferente da quantidade de IDs solicitados.
         * 
         * Por que usei size() aqui? Se os tamanhos forem diferentes, significa que pelo menos um ID enviado não correponde a uma 
         * especialidade existente no BD, resultando na exceção. */
        if (especialidades.size() != request.especialidadeIds().size()) {
            throw new BusinessException("Um ou mais especialidades informadas não existem");
        }

        // Vincula as especialidades ao médico
        medico.setEspecialidades(especialidades);

        return medicoMapper.toResponse(medicoRepository.save(medico));
    }


    // --- LISTAR ----------------------------------------------------

    /**
     * Lista todos os médicos ativos com paginação.
     * 
     * @param pageable configurações de paginação (size, page, sort)
     * @return Page<MedicoResponse> com médicos ativos paginados
     */

    @Transactional(readOnly = true)
    public Page<MedicoResponse> listarAtivos(Pageable pageable) {
        return medicoRepository.findByAtivoTrue(pageable).map(medicoMapper::toResponse);
    }

    // --- BUSCAR POR ID ----------------------------------------------------

    /**
     * Busca médico por ID específico ou lança ResourceNotFoundException.
     * 
     * @param id identificador único do médico
     * @return MedicoResponse com dados do médico
     */

    @Transactional(readOnly = true)
    public MedicoResponse buscarPorId(Long id) {
        return medicoMapper.toResponse(medicoRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Medico", id)));
    }

    // --- BUSCAR POR NOME ----------------------------------------------------

    /**
     * Busca médicos por nome (busca parcial) com paginação.
     * 
     * @param nome termo para busca no nome do médico (like %nome%)
     * @param pageable configurações de paginação
     * @return Page<MedicoResponse> com resultados da busca
     */

    @Transactional(readOnly = true)
    public Page<MedicoResponse> buscarPorNome(String nome, Pageable pageable) {
        return medicoRepository.buscarPorNome(nome, pageable).map(medicoMapper::toResponse);
    }

    // --- ATUALIZAR ----------------------------------------------------

    /**
     * Atualiza dados do médico (nome, email, telefone, especialidades).
     * CRM não é alterado. Validações de unicidade e existência.
     * 
     * @param id identificador do médico a atualizar
     * @param request dados atualizados (parcial ou completo)
     * @return MedicoResponse atualizado
     */

    @Transactional
    public MedicoResponse atualizar(Long id, MedicoRequest request) {
        
        Medico medico = medicoRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Medico", id));

        // Email único ignorando o proprio registro
        if (request.email() != null && !request.email().isBlank() && medicoRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BusinessException("Email já cadastrado por outro médico");
        }

        // Atualiza campos básicos (CRM não altera)
        medicoMapper.updateFromRequest(request, medico);

        // Atualiza a lista de especialidades se informadas
        if (request.especialidadeIds() != null && !request.especialidadeIds().isEmpty()) {

            List<Especialidade> especialidades = especialidadeRepository.findAllByIdIn(request.especialidadeIds());

            if (especialidades.size() != request.especialidadeIds().size()) {
                throw new BusinessException("Uma ou mais especialidades informadas nao existem");
            }
            
            medico.setEspecialidades(especialidades);
        }

        return medicoMapper.toResponse(medicoRepository.save(medico));
    }

    // --- DESATIVAR ----------------------------------------------------

    /**
     * Desativa um médico (seta ativo = false).
     * Não permite desativar se já desativado.
     * 
     * @param id identificador do médico a desativar
     */

    @Transactional
    public void desativar(Long id) {

        Medico medico = medicoRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Medico", id));

        if (!medico.getAtivo()) {
            throw new BusinessException("Medico ja esta desativado");
        }

        medico.setAtivo(false);
        medicoRepository.save(medico);
    }

    // --- ADICIONAR ESPECIALIDADE ----------------------------------------------------

    /**
     * Adiciona especialidade ao médico sem remover as existentes.
     * Não permite duplicatas.
     * 
     * @param medicoId ID do médico
     * @param especialidadeId ID da especialidade a adicionar
     * @return MedicoResponse atualizado
     */

    @Transactional
    public MedicoResponse adicionarEspecialidade(Long medicoId, Long especialidadeId) {

        Medico medico = medicoRepository.findById(medicoId).orElseThrow(
            () -> new ResourceNotFoundException("Medico", medicoId));

        Especialidade especialidade = especialidadeRepository.findById(especialidadeId).orElseThrow(
            () -> new ResourceNotFoundException("Especialidade", especialidadeId));

        // Regra 1: Não adicionar especialidade já existente
        if (medico.getEspecialidades().contains(especialidade)) {
            throw new BusinessException("Medico ja possui esta especialidade");
        }

        medico.getEspecialidades().add(especialidade);
        return medicoMapper.toResponse(medicoRepository.save(medico));
    }

    // --- REMOVER ESPECIALIDADE ----------------------------------------------------

    /**
     * Remove uma especialidade do médico mantendo as demais.
     * Não permite remover se for a única especialidade.
     * 
     * @param medicoId ID do médico
     * @param especialidadeId ID da especialidade a remover
     * @return MedicoResponse atualizado
     */

    @Transactional
    public MedicoResponse removerEspecialidade(Long medicoId, Long especialidadeId) {

        Medico medico = medicoRepository.findById(medicoId).orElseThrow(
            () -> new ResourceNotFoundException("Medico", medicoId));

        Especialidade especialidade = especialidadeRepository.findById(especialidadeId).orElseThrow(
            () -> new ResourceNotFoundException("Especialidade", especialidadeId));

        // Regra 1: Não pode ficar sem nenhuma especialidade
        if (medico.getEspecialidades().size() <= 1) {
            throw new BusinessException("Medico deve manter ao menos uma especialidade");
        }

        medico.getEspecialidades().remove(especialidade);
        return medicoMapper.toResponse(medicoRepository.save(medico));
    }

    // --- LISTAR ESPECIALIDADE ----------------------------------------------------

    /**
     * Lista todas as especialidades disponíveis no sistema.
     * Usado para popular o select de especialidades no frontend.
     * 
     * @return EspecialidadeResponse listado
     */
    @Transactional(readOnly = true)
    public List<EspecialidadeResponse> listarEspecialidades() {
        return especialidadeRepository.findAll()
            .stream()
            .map(e -> new EspecialidadeResponse(e.getId(), e.getNome()))
            .toList();
    }
}
