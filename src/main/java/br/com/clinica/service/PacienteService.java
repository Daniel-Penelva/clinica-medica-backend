package br.com.clinica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.clinica.domain.model.Convenio;
import br.com.clinica.domain.model.Paciente;
import br.com.clinica.dto.request.PacienteRequest;
import br.com.clinica.dto.response.PacienteResponse;
import br.com.clinica.exception.BusinessException;
import br.com.clinica.exception.ResourceNotFoundException;
import br.com.clinica.mapper.PacienteMapper;
import br.com.clinica.repository.ConvenioRepository;
import br.com.clinica.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ConvenioRepository convenioRepository;
    private final PacienteMapper pacienteMapper;

    // --- CADASTRAR ----------------------------------------------------

    /**
     * Cadastra um novo paciente.
     * Regras: CPF único, email único (se informado), convenio válido (se informado)
     * 
     * @param request DTO com os dados do paciente a ser cadastrado
     * @return PacienteResponse com os dados do paciente criado e salvo
     */
    @Transactional
    public PacienteResponse cadastrar(PacienteRequest request) {

        // Regra 1. CPF não pode ser duplicado
        if (pacienteRepository.existsByCpf(request.cpf())) {
            throw new BusinessException("CPF já cadastrado no sistema");
        }

        // Regra 2. Email não pode ser duplicado (quando informado)
        if (request.email() != null && !request.email().isBlank()
                && pacienteRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado no sistema");

        }

        // Converte o DTO para entidade
        Paciente paciente = pacienteMapper.toEntity(request);

        // Vincula o convenio se informado
        if (request.convenioId() != null) {
            Convenio convenio = convenioRepository.findById(request.convenioId()).orElseThrow(
                    () -> new ResourceNotFoundException("Convenio", request.convenioId()));

            paciente.setConvenio(convenio);
        }

        return pacienteMapper.toResponse(pacienteRepository.save(paciente));
    }


    // --- LISTAR ----------------------------------------------------

    /**
     * Listar todos os pacientes ativos com paginação
     * Pageable permite controlar página, tamanho e ordenação pela URL
     * 
     * @param pageable paginação e ordenação
     * @return página de pacientes ativos filtrados
     */
    @Transactional(readOnly = true)  // readOnly = true é para o Spring e o banco saber que não ha escrita
    public Page<PacienteResponse> listarAtivos(Pageable pageable) {
        return pacienteRepository.findByAtivoTrue(pageable).map(pacienteMapper::toResponse);
    }


    // --- BUSCAR POR ID ----------------------------------------------------

    /**
     * Busca um paciente pelo ID
     * Lança 404 se não encontrado
     * 
     * @param id Id do paciente a ser buscado
     * @return PacienteResponse buscado por id
     */
    @Transactional(readOnly = true)
    public PacienteResponse buscarPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Paciente", id));

        return pacienteMapper.toResponse(paciente);
    }


    // --- BUSCAR POR NOME ----------------------------------------------------

    /**
     * Busca pacientes por nome (busca parcial, case-insensitive).
     * Ex: buscar 'jo' retorna 'Joao', 'Joana' etc.
     * 
     * @param nome     termo de busca (parcial)
     * @param pageable paginação e ordenação
     * @return página de pacientes ativos filtrados
     */
    @Transactional(readOnly = true)
    public Page<PacienteResponse> buscarPorNome(String nome, Pageable pageable) {
        return pacienteRepository.buscarPorNome(nome, pageable).map(pacienteMapper::toResponse);
    }


    // --- ATUALIZAR ----------------------------------------------------

    /**
     * Atualiza os dados de um paciente existente.
     * Regras: Paciente existe, CPF único (exceto o atual), email único (exceto o atual),
     * convenio válido (se alterado).
     * Campos não informados no request mantêm os valores atuais.
     * 
     * @param id      ID do paciente a atualizar
     * @param request dados a serem atualizados (campos opcionais)
     * @return PacienteResponse atualizado
     */
    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest request) {

        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Paciente", id));

        // Regra 1: Email unico (ignorando o proprio registro)
        if (request.email() != null && !request.email().isBlank() && pacienteRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BusinessException("Email ja cadastrado por outro paciente");
        }

        // Atualizar os campos (o mapper não cria objeto novo, atualiza o existente)
        pacienteMapper.updateFromRequest(request, paciente);

        // Atualizar convenio
        if (request.convenioId() != null) {
            Convenio convenio = convenioRepository.findById(request.convenioId()).orElseThrow(
                    () -> new ResourceNotFoundException("Convenio", request.convenioId()));

            paciente.setConvenio(convenio);
        } else {
            paciente.setConvenio(null); // remove o convenio se não informado
        }

        return pacienteMapper.toResponse(pacienteRepository.save(paciente));
    }


    // --- DESATIVAR  ----------------------------------------------------

    /**
     * Desativa um paciente (exclusão lógica)
     * O registro permanece no banco de dados com ativo = false
     * Histórico de consultas e prontuários é preservado
     * 
     * @param id ID do paciente para o desativar
     */
    @Transactional
    public void desativar(Long id) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Paciente", id));

        if (!paciente.getAtivo()) {
            throw new BusinessException("Paciente já está desativado");
        }

        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }
}

/**
 * A classe PacienteService contém todas as regras de negócio.
*/
