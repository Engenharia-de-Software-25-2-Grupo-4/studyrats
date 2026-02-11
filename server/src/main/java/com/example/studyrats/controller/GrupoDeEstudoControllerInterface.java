package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConvitePostRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Grupo de Estudo", description = "Operacoes sobre grupos de estudo")
@RequestMapping(value = "/grupos", produces = MediaType.APPLICATION_JSON_VALUE)
public interface GrupoDeEstudoControllerInterface {

    @Operation(summary = "Criar grupo de estudo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    GrupoDeEstudoResponseDTO criarGrupo(@RequestBody @Valid GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request);

    @Operation(summary = "Buscar grupo por id")
    @GetMapping("/{id}")
    GrupoDeEstudoResponseDTO buscarPorId(@PathVariable UUID id);

    @Operation(summary = "Atualizar grupo")
    @PutMapping("/{id}")
    GrupoDeEstudoResponseDTO atualizar(@PathVariable UUID id, @RequestBody @Valid GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request);

    @Operation(summary = "Remover grupo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remover(@PathVariable UUID id, HttpServletRequest request);

    @Operation(summary = "Listar grupos do usuario autenticado")
    @GetMapping
    List<GrupoDeEstudoResponseDTO> listarPorUsuario(HttpServletRequest request);

    @Operation(summary = "Convidar usuario para o grupo")
    @PostMapping("/{id}/convites")
    @ResponseStatus(HttpStatus.CREATED)
    void convidar(@PathVariable UUID id, @RequestBody @Valid ConvitePostRequestDTO dto, HttpServletRequest request);

    @Operation(summary = "Aceitar convite")
    @PostMapping("/convites/{idConvite}/aceitar")
    void aceitarConvite(@PathVariable UUID idConvite, HttpServletRequest request);

    @Operation(summary = "Listar convites do usuario autenticado")
    @GetMapping("/convites")
    List<?> listarConvites(HttpServletRequest request);

    @Operation(summary = "Remover check-in inválido (admins)")
    @DeleteMapping("/{idGrupo}/checkins/{idSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removerCheckinInvalido(@PathVariable UUID idGrupo, @PathVariable UUID idSessao, HttpServletRequest request);
}
