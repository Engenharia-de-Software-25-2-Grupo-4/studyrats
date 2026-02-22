package com.example.studyrats.controller;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Disciplina", description = "Operacoes sobre disciplinas")
@RequestMapping(value = "/disciplinas", produces = MediaType.APPLICATION_JSON_VALUE)
public interface DisciplinaControllerImpl {

    @Operation(summary = "Listar todas as disciplinas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping
    List<DisciplinaResponseDTO> listarDisciplinas(HttpServletRequest request);

    @Operation(summary = "Listar disciplinas do usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping("/minhas")
    List<DisciplinaResponseDTO> listarDisciplinasPorUsuario(HttpServletRequest request);

    @Operation(summary = "Listar disciplinas de um grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
    })
    @GetMapping("/grupo/{idGrupo}")
    List<DisciplinaResponseDTO> listarDisciplinasPorGrupo(@PathVariable UUID idGrupo, HttpServletRequest request);

    @Operation(summary = "Listar disciplinas do usuario autenticado em um grupo especifico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
    })
    @GetMapping("/grupo/{idGrupo}/minhas")
    List<DisciplinaResponseDTO> listarDisciplinasPorUsuarioEmGrupo(@PathVariable UUID idGrupo, HttpServletRequest request);
}