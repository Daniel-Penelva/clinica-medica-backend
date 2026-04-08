package br.com.clinica.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.request.MedicoRequest;
import br.com.clinica.dto.response.MedicoResponse;
import br.com.clinica.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medicos")
@RequiredArgsConstructor
@Tag(name = "Medicos", description = "Gerenciamento de medicos e especialidades")
public class MedicoController {

    private final MedicoService medicoService;

    // --- POST: Cadastrar ----------------------------------------------------

    /**
     * Cadastra um novo médico.
     * Endpoint: POST /api/v1/medicos
     * Requer role ADMIN. Retorna HTTP 201 CREATED.
     * 
     * @param request dados do médico (nome, crm, email, telefone, especialidadeIds[])
     * @return MedicoResponse do médico criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar medico", description = "Cadastra um novo medico no sistema.")
    public MedicoResponse cadastrar(@Valid @RequestBody MedicoRequest request) {
        return medicoService.cadastrar(request);
    }

    // --- GET: Listar todos ----------------------------------------------------

    /**
     * Lista médicos ativos paginados.
     * Endpoint: GET /api/v1/medicos
     * Requer autenticação. Default: size=10, sort=nome.
     * 
     * @param pageable paginação (size, page, sort)
     * @return Page<MedicoResponse> médicos ativos
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar medicos ativos", description = "Lista todos os medicos ativos, paginados e ordenados por nome.")
    public Page<MedicoResponse> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return medicoService.listarAtivos(pageable);
    }

    // --- GET: Buscar por ID ----------------------------------------------------

    /**
     * Busca médico por ID.
     * Endpoint: GET /api/v1/medicos/{id}
     * Requer autenticação.
     * 
     * @param id ID do médico
     * @return MedicoResponse do médico encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar medico por ID", description = "Busca um medico específico pelo seu ID.")
    public MedicoResponse buscarPorId(@PathVariable Long id) {
        return medicoService.buscarPorId(id);
    }

    // --- GET: Buscar por nome ----------------------------------------------------

    /**
     * Busca médicos por nome (parcial).
     * Endpoint: GET /api/v1/medicos/buscar?nome=parteDoNome
     * Requer autenticação. Apenas médicos ativos.
     * 
     * @param nome termo de busca no nome
     * @param pageable paginação
     * @return Page<MedicoResponse> resultados
     */
    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar medicos por nome", description = "A busca é realizada apenas em medicos ativos e retorna os resultados paginados.")
    public Page<MedicoResponse> buscarPorNome(
            @RequestParam String nome,
            @PageableDefault(size = 10, sort = "nome")
            Pageable pageable) {
        return medicoService.buscarPorNome(nome, pageable);
    }

    // --- PUT: Atualizar ----------------------------------------------------

    /**
     * Atualiza dados do médico.
     * Endpoint: PUT /api/v1/medicos/{id}
     * Requer role ADMIN.
     * 
     * @param id ID do médico
     * @param request novos dados (nome, email, telefone, especialidades opcionais)
     * @return MedicoResponse atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar medico", description = "Atualiza os dados de um medico existente.")
    public MedicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody MedicoRequest request) {
        return medicoService.atualizar(id, request);
    }

    // --- DELETE: Desativar ----------------------------------------------------

    /**
     * Desativa médico logicamente.
     * Endpoint: DELETE /api/v1/medicos/{id}
     * Requer role ADMIN. Retorna HTTP 204 NO_CONTENT.
     * 
     * @param id ID do médico a desativar
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar medico", description = "Desativa logicamente um medico no sistema.")
    public void desativar(@PathVariable Long id) {
        medicoService.desativar(id);
    }

    // --- Gerenciamento de Especialidades (ManyToMany) ----------------------------------------------------

    // --- POST: Adicionar especialidade ao médico ----------------------------------------------------

    /**
     * Adiciona especialidade ao médico.
     * Endpoint: POST /api/v1/medicos/{id}/especialidades/{especialidadeId}
     * Requer role ADMIN. Não permite duplicatas.
     * 
     * @param id ID do médico
     * @param especialidadeId ID da especialidade
     * @return MedicoResponse atualizado
     */
    @PostMapping("/{id}/especialidades/{especialidadeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Adicionar especialidade ao medico", description = "Cadastra uma especialidade ao médico sem alterar as existentes.")
    public MedicoResponse adicionarEspecialidade(@PathVariable Long id, @PathVariable Long especialidadeId) {
        return medicoService.adicionarEspecialidade(id, especialidadeId);
    }

    // --- DELETE: Remover especialidade do médico ----------------------------------------------------

    /**
     * Remove especialidade do médico.
     * Endpoint: DELETE /api/v1/medicos/{id}/especialidades/{especialidadeId}
     * Requer role ADMIN. Não permite ficar sem especialidades.
     * 
     * @param id ID do médico
     * @param especialidadeId ID da especialidade
     * @return MedicoResponse atualizado
     */
    @DeleteMapping("/{id}/especialidades/{especialidadeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover especialidade do medico", description = "Remove logicamente uma especialidade do medico no sistema.")
    public MedicoResponse removerEspecialidade(@PathVariable Long id, @PathVariable Long especialidadeId) {
        return medicoService.removerEspecialidade(id, especialidadeId);
    }
}
