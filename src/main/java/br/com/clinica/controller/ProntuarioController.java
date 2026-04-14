package br.com.clinica.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.request.ProntuarioRequest;
import br.com.clinica.dto.response.ProntuarioResponse;
import br.com.clinica.service.ProntuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/prontuarios")
@RequiredArgsConstructor
@Tag(name ="Prontuarios", description = "Prontuario eletronico das consultas")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;
    
    /**
     * Cria um novo prontuário para consulta realizada.
     * 
     * <p>Validações realizadas no service:</p>
     * <ul>
     * <li>Consulta existe e tem status REALIZADA</li>
     * <li>Consulta ainda não possui prontuário (relação 1:1)</li>
     * </ul>
     * 
     * @param request Dados clínicos do prontuário
     * @return        Prontuário criado com ID gerado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MEDICO')")
    @Operation(summary = "Criar prontuario", description = "Cria prontuário eletrônico para consulta REALIZADA.")
    public ProntuarioResponse criar(@Valid @RequestBody ProntuarioRequest request) {
        return prontuarioService.criar(request);
    }

    /**
     * Busca prontuário pela ID da consulta associada.
     * 
     * <p>Retorna o prontuário da consulta específica (relação 1:1).</p>
     * 
     * @param consultaId ID da consulta
     * @return           Dados completos do prontuário
     */
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ADMIN')")
    @Operation(summary = "Buscar prontuario pela consulta", description = "Recupera o prontuário eletrônico de uma consulta específica. ")
    public ProntuarioResponse buscarPorConsulta(@PathVariable Long consultaId) {
        return prontuarioService.buscarPorConsulta(consultaId);
    }

    /**
     * Atualiza dados do prontuário existente.
     * 
     * <p>Apenas o médico responsável pela consulta pode atualizar.</p>
     * 
     * @param id      ID do prontuário
     * @param request Dados atualizados do prontuário
     * @return        Prontuário atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO')")
    @Operation(summary = "Atualizar prontuario", description = "Atualiza informações clínicas do prontuário existente.")
    public ProntuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProntuarioRequest request) {
        return prontuarioService.atualizar(id, request);
    }
}
