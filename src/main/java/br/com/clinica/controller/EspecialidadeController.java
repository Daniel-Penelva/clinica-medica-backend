package br.com.clinica.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.clinica.dto.response.EspecialidadeResponse;
import br.com.clinica.service.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/especialidades")
@RequiredArgsConstructor
@Tag(name = "Especialidades", description = "Lista de especialidades medicas")
public class EspecialidadeController {

    private final MedicoService medicoService;

    // --- GET: Listar todos ----------------------------------------------------

    /**
     * Lista todas as especialidades disponíveis.
     * Usado pelo frontend para popular selects e filtros.
     * 
     * @return Página de EspecialidadeResponse contendo a lista de especialidades
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar especialidades", description = "Lista todos as especialidades")
    public List<EspecialidadeResponse> listar() {
        return medicoService.listarEspecialidades();
    }

}
