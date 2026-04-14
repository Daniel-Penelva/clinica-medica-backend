package br.com.clinica.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.request.CancelamentoRequest;
import br.com.clinica.dto.request.ConsultaRequest;
import br.com.clinica.dto.response.ConsultaResponse;
import br.com.clinica.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Agendamento e gestão de consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    /**
     * Agenda uma nova consulta no sistema.
     * 
     * @param request Dados da consulta (paciente, médico, data/hora)
     * @return        Consulta criada com ID e status AGENDADA
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Agendar Consulta", description = "Cria uma nova consulta validando paciente e médico ativos, sem conflito de horário.")
    public ConsultaResponse agendar(@Valid @RequestBody ConsultaRequest request) {
        return consultaService.agendar(request);
    }

    /**
     * Lista todas as consultas com paginação.
     * 
     * @param pageable Parâmetros de paginação (tamanho padrão: 10, ordenação por dataHora)
     * @return         Página de consultas ordenadas por data e hora
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar consultas", description = "Retorna todas as consultas do sistema, paginadas e ordenadas por data/hora ascendente.")
    public Page<ConsultaResponse> listar(@PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        return consultaService.listar(pageable);
    }

    /**
     * Busca consulta específica por ID.
     * 
     * @param id ID da consulta
     * @return   Dados completos da consulta
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar consulta por ID", description = "Recupera uma consulta específica pelo seu identificador único.")
    public ConsultaResponse buscarPorId(@PathVariable Long id) {
        return consultaService.buscarPorId(id);
    }

    /**
     * Lista consultas de um paciente específico.
     * 
     * @param pacienteId ID do paciente
     * @param pageable   Parâmetros de paginação
     * @return           Página de consultas do paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar consultas de um paciente", description = "Lista as consultas de um paciente específico, paginados e ordenados por data e hora.")
    public Page<ConsultaResponse> listarPorPaciente(@PathVariable Long pacienteId,
            @PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        return consultaService.listarPorPaciente(pacienteId, pageable);
    }

    /**
     * Lista consultas de um médico específico.
     * 
     * @param medicoId ID do médico
     * @param pageable Parâmetros de paginação
     * @return         Página de consultas do médico
     */
    @GetMapping("/medico/{medicoId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar consultas de um medico", description = "Lista todas as consultas de um médico específico, paginadas e ordenadas por data/hora.")
    public Page<ConsultaResponse> listarPorMedico(@PathVariable Long medicoId,
            @PageableDefault(size = 10, sort = "dataHora") Pageable pageable) {
        return consultaService.listarPorMedico(medicoId, pageable);
    }

    /**
     * Confirma presença do paciente na consulta.
     * 
     * <p>Muda status de AGENDADA para CONFIRMADA</p>
     * 
     * @param id ID da cosnulta
     * @return Consulta confirmada
     */
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Confirmar presenca na consulta", description = "Confirma a presença do paciente na consulta (AGENDADA -> CONFIRMADA).")
    public ConsultaResponse confirmar(@PathVariable Long id) {
        return consultaService.confirmar(id);
    }

    /**
     * Cancela consulta com motivo especificado.
     * 
     * @param id ID da consulta
     * @param request Motivo do cancelamento
     * @return   Consulta cancelada
     */
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Cancelar consulta", description = "Cancela consulta registrando motivo (AGENDADA/CONFIRMADA -> CANCELADA).")
    public ConsultaResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelamentoRequest request) {
        return consultaService.cancelar(id, request);
    }

    /**
     * Marca consulta como realizada pelo médico.
     * 
     * <p>Muda status para REALIZADA (AGENDADA/CONFIRMADA -> REALIZADA)</p>
     * 
     * @param id ID da consulta
     * @return Consulta realizada
     */
    @PatchMapping("/{id}/realizar")
    @PreAuthorize("hasRole('MEDICO')")
    @Operation(summary = "Marcar consulta como realizada", description = "Médico marca a consulta como realizada após atendimento (AGENDADA/CONFIRMADA -> REALIZADA).")
    public ConsultaResponse realizar(@PathVariable Long id) {
        return consultaService.realizar(id);
    }

    /**
     * Registra não comparecimento do paciente.
     * 
     * <p>Muda status para NÃO COMPARECEU (AGENDADA/CONFIRMADA -> NÃO COMPARECEU)</p>
     * 
     * @param id ID da consulta
     * @return Consulta não comparecido
     */
    @PatchMapping("/{id}/nao-compareceu")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @Operation(summary = "Registrar nao comparecimento", description = "Registra ausência do paciente na consulta (AGENDADA/CONFIRMADA -> NÃO COMPARECEU).")
    public ConsultaResponse naoCompareceu(@PathVariable Long id) {
        return consultaService.registrarNaoCompareceu(id);
    }
}
