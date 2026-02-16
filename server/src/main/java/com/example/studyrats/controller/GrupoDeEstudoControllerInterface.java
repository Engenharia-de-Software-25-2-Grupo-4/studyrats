package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConviteResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Grupo de Estudo", description = "Operacoes sobre grupos de estudo")
@RequestMapping(
    value = "/grupos", 
    produces = MediaType.APPLICATION_JSON_VALUE
)
public interface GrupoDeEstudoControllerInterface {

    @Operation(summary = "Criar grupo de estudo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Grupo criado"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Estudante nao encontrado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    GrupoDeEstudoResponseDTO createGrupoDeEstudo(
        @RequestBody @Valid GrupoDeEstudoPostPutRequestDTO dto, 
        HttpServletRequest request
    );

    @Operation(summary = "Buscar grupo por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupo encontrado"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
    })
    @GetMapping("/{id}")
    GrupoDeEstudoResponseDTO getGrupoDeEstudo(
        @PathVariable UUID id,
        HttpServletRequest request
    );

    @Operation(summary = "Atualizar grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Grupo atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado ou permissao insuficiente")
    })
    @PutMapping("/{id}")
    GrupoDeEstudoResponseDTO updateGrupoDeEstudo(
        @Parameter(description = "ID do grupo", required = true)
        @PathVariable UUID id, 
        @RequestBody @Valid GrupoDeEstudoPostPutRequestDTO dto, 
        HttpServletRequest request
    );

    @Operation(summary = "Remover grupo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Grupo removido"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado ou permissao insuficiente")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteGrupoDeEstudo(
        @Parameter(description = "ID do grupo", required = true)
        @PathVariable UUID id, 
        HttpServletRequest request
    );

    @Operation(summary = "Listar grupos do usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping
    List<GrupoDeEstudoResponseDTO> listGrupoDeEstudosByUser(HttpServletRequest request);

    @Operation(summary = "Gerar link de convite (Admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas admin pode gerar convites"),
            @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
    })
    @PostMapping("/{idGrupo}/convites/gerar")
    String generateInviteLink(
            @Parameter(description = "ID do grupo", required = true)
            @PathVariable UUID idGrupo,
            HttpServletRequest request
    );

    @Operation(summary = "Validar link de convite (Obter info do grupo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convite valido"),
            @ApiResponse(responseCode = "404", description = "Convite invalido ou expirado")
    })
    @GetMapping("/convites/{token}")
    ConviteResponseDTO validateInvite(
            @Parameter(description = "Token do convite", required = true)
            @PathVariable String token,
            HttpServletRequest request
    );

    @Operation(summary = "Entrar no grupo via link")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Usuario ja pertence ao grupo ou convite expirado"),
            @ApiResponse(responseCode = "404", description = "Convite invalido")
    })
    @PostMapping("/convites/{token}/entrar")
    void joinGroupViaInvite(
            @Parameter(description = "Token do convite", required = true)
            @PathVariable String token,
            HttpServletRequest request
    );

    @Operation(summary = "Remover check-in inválido (admins)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Check-in removido"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado"),
            @ApiResponse(responseCode = "404", description = "Grupo ou Sessao nao encontrada")
    })
    @DeleteMapping("/{idGrupo}/checkins/{idSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeInvalidCheckin(
            @Parameter(description = "ID do grupo", required = true)
            @PathVariable UUID idGrupo,
            @Parameter(description = "ID da sessao", required = true)
            @PathVariable UUID idSessao,
            HttpServletRequest request
    );
}