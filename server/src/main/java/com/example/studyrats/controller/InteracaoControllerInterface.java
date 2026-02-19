package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoPostPutRequestDTO;
import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoResponseDTO;
import com.example.studyrats.dto.Interacao.Reacao.ReacaoSessaoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Interações da Sessão de Estudo", description = "Endpoints de comentários e reações em uma sessão de estudo.")
@RequestMapping("/sessaoDeEstudo/{idSessao}")
public interface InteracaoControllerInterface {

    @Operation(
        summary = "Criar comentário na sessão",
        description = "Cria um comentário vinculado à sessão informada. O autor é o usuário autenticado (Firebase)."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Comentário criado com sucesso",
        content = @Content(schema = @Schema(implementation = ComentarioSessaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "Sem permissão / não é membro do grupo", content = @Content)
    @ApiResponse(responseCode = "404", description = "Sessão ou estudante não encontrado", content = @Content)
    @PostMapping("/comentarios")
    @ResponseStatus(HttpStatus.CREATED)
    ComentarioSessaoResponseDTO criarComentarioSessao(
        @Parameter(description = "ID da sessão de estudo", required = true)
        @PathVariable UUID idSessao,

        @RequestBody(
            required = true,
            description = "Dados do comentário",
            content = @Content(schema = @Schema(implementation = ComentarioSessaoPostPutRequestDTO.class))
        )
        @Valid @org.springframework.web.bind.annotation.RequestBody ComentarioSessaoPostPutRequestDTO dto,

        HttpServletRequest request
    );

    @Operation(
        summary = "Listar comentários da sessão",
        description = "Lista os comentários da sessão em ordem crescente de horário."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de comentários",
        content = @Content(schema = @Schema(implementation = ComentarioSessaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "Sem permissão / não é membro do grupo", content = @Content)
    @ApiResponse(responseCode = "404", description = "Sessão ou estudante não encontrado", content = @Content)
    @GetMapping("/comentarios")
    List<ComentarioSessaoResponseDTO> listarComentariosSessao(
        @Parameter(description = "ID da sessão de estudo", required = true)
        @PathVariable UUID idSessao,
        HttpServletRequest request
    );

    @Operation(
        summary = "Deletar comentário da sessão",
        description = "Deleta um comentário. Permitido para o autor do comentário ou admin do grupo da sessão."
    )
    @ApiResponse(responseCode = "204", description = "Comentário deletado com sucesso", content = @Content)
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "Sem permissão para deletar", content = @Content)
    @ApiResponse(responseCode = "404", description = "Comentário/estudante/sessão não encontrado", content = @Content)
    @DeleteMapping("/comentarios/{idComentario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletarComentarioSessao(
        @Parameter(description = "ID do comentário", required = true)
        @PathVariable UUID idComentario,
        HttpServletRequest request
    );

    @Operation(
        summary = "Toggle de reação na sessão",
        description = "Se o usuário já reagiu, remove a reação. Caso contrário, cria a reação."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Estado de reação e total atualizado",
        content = @Content(schema = @Schema(implementation = ReacaoSessaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "Sem permissão / não é membro do grupo", content = @Content)
    @ApiResponse(responseCode = "404", description = "Sessão ou estudante não encontrado", content = @Content)
    @PostMapping("/reacoes/toggle")
    ReacaoSessaoResponseDTO reagirOuTirarReacaoSessao(
        @Parameter(description = "ID da sessão de estudo", required = true)
        @PathVariable UUID idSessao,
        HttpServletRequest request
    );

    @Operation(
        summary = "Listar reações da sessão",
        description = "Retorna se o usuário autenticado reagiu e o total de reações na sessão."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Estado de reação e total",
        content = @Content(schema = @Schema(implementation = ReacaoSessaoResponseDTO.class))
    )
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "403", description = "Sem permissão / não é membro do grupo", content = @Content)
    @ApiResponse(responseCode = "404", description = "Sessão ou estudante não encontrado", content = @Content)
    @GetMapping("/reacoes")
    ReacaoSessaoResponseDTO listarReacoesSessao(
        @Parameter(description = "ID da sessão de estudo", required = true)
        @PathVariable UUID idSessao,
        HttpServletRequest request
    );
}
