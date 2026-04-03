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

import br.com.clinica.dto.request.PacienteRequest;
import br.com.clinica.dto.response.PacienteResponse;
import br.com.clinica.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pacientes")
@RequiredArgsConstructor
@Tag(name = "Pacientes", description = "Gerenciamento de pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    // --- POST: Cadastrar ----------------------------------------------------

    /**
     * Cadastra um novo paciente no sistema.
     * Os campos devem atender às validações definidas na classe PacienteRequest.
     * 
     * @param request Objeto de requisição contendo os dados do paciente a ser cadastrado.
     * @return PacienteResponse contendo os dados do paciente recém-cadastrado,
     *         incluindo o ID gerado e demais informações adicionais conforme o mapeamento.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Cadastrar paciente", description = "Cadastra um novo paciente no sistema.")
    public PacienteResponse cadastrar(@Valid @RequestBody PacienteRequest request) {
        return pacienteService.cadastrar(request);
    }

    // --- GET: Listar todos ----------------------------------------------------

    /**
     * Lista todos os pacientes ativos, paginados e ordenados por nome.
     * 
     * @param pageable Objeto que define a paginação (página, tamanho) e ordenação dos resultados.
     *                 Por padrão, utiliza tamanho 10 e ordenação por nome em ordem ascendente.
     * @return Página de PacienteResponse contendo a lista de pacientes ativos
     *         para a página solicitada, incluindo informações de paginação.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar pacientes ativos", description = "Lista todos os pacientes ativos, paginados e ordenados por nome.")
    public Page<PacienteResponse> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return pacienteService.listarAtivos(pageable);
    }

    // ── GET: Buscar por ID ────────────────────────────────────────

    /**
     * Busca um paciente específico pelo seu ID.
     * 
     * @param id Identificador único do paciente. Deve ser um valor longo maior que zero.
     * @return PacienteResponse contendo os dados do paciente correspondente ao ID informado.
     *         Em caso de paciente não encontrado, o service deve lançar exceção específica.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar paciente por ID", description = "Busca um paciente específico pelo seu ID.")
    public PacienteResponse buscarPorId(@PathVariable Long id) {
        return pacienteService.buscarPorId(id);
    }

    // ── GET: Buscar por nome ──────────────────────────────────────

    /**
     * Busca pacientes pelo nome, aplicando filtro parcial (contendo o termo informado).
     * A busca é realizada apenas em pacientes ativos e retorna os resultados paginados.
     * 
     * @param nome Termo de busca para o nome do paciente. Aceita filtro parcial (LIKE).
     *             Deve ser uma string não nula e não vazia; se necessário, a validação deve ser feita no service.
     * @param pageable Objeto que define a paginação (página, tamanho) e ordenação dos resultados.
 *                     Por padrão, utiliza tamanho 10 e ordenação por nome em ordem ascendente.
     * @return Página de PacienteResponse contendo a lista de pacientes cujo nome corresponde ao filtro,
 *             para a página solicitada, incluindo informações de paginação.
     */
    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar pacientes por nome", description = "A busca é realizada apenas em pacientes ativos e retorna os resultados paginados.")
    public Page<PacienteResponse> buscarPorNome(@RequestParam String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return pacienteService.buscarPorNome(nome, pageable);
    }


    // ── PUT: Atualizar ────────────────────────────────────────────

    /**
     * Atualiza os dados de um paciente existente.
     * 
     * @param id Identificador único do paciente a ser atualizado.
     *           Deve corresponder a um paciente existente no sistema.
     * @param request Objeto de requisição contendo os novos dados do paciente.
     *                Os campos devem atender às validações definidas na classe PacienteRequest.
     * @return PacienteResponse contendo os dados do paciente após a atualização, refletindo as alterações realizadas.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Atualizar paciente", description = "Atualiza os dados de um paciente existente.")
    public PacienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest request) {
        return pacienteService.atualizar(id, request);
    }


    // ── DELETE: Desativar ─────────────────────────────────────────

    /**
     * Desativa logicamente um paciente no sistema.
     * 
     * @param id Identificador único do paciente a ser desativado.
     *           O paciente continua existindo no banco, porém passa a ser considerado inativo.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar paciente", description = "Desativa logicamente um paciente no sistema.")
    public void desativar(@PathVariable Long id) {
        pacienteService.desativar(id);
    }
}
