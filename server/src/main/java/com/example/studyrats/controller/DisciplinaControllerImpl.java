package com.example.studyrats.controller;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
}