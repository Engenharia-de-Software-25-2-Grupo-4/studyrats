package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;

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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Sessao de Estudo", description = "Operacoes de sessao de estudo")
@RequestMapping(
    value = "/sessaoDeEstudo",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public interface SessaoDeEstudoControllerInterface {
    @Operation(summary = "Criar sessao de estudo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sessao criada"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    })
    @PostMapping("/{idGrupo}")
    @ResponseStatus(HttpStatus.CREATED)
    SessaoDeEstudoResponseDTO createSessaoDeEstudo(
        @Parameter(description = "ID do grupo", required = true)
        @PathVariable UUID idGrupo,
        @RequestBody @Valid SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO,
        HttpServletRequest request
    );

    @Operation(summary = "Buscar sessao de estudo por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sessao encontrada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Sessao nao encontrada ou ele não é o criador daquela sessao")
    })
    @GetMapping("/{idSessao}")
    SessaoDeEstudoResponseDTO getSessaoDeEstudo(
        @Parameter(description = "ID da sessao", required = true)
        @PathVariable UUID idSessao,
        HttpServletRequest request
    );

    @Operation(summary = "Atualizar sessao de estudo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sessao atualizada"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Sessao nao encontrada ou ele não é o criador daquela sessao")
    })
    @PutMapping("/{idSessao}")
    SessaoDeEstudoResponseDTO updateSessaoDeEstudo(
        @Parameter(description = "ID da sessao", required = true)
        @PathVariable UUID idSessao,
        @RequestBody @Valid SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO,
        HttpServletRequest request
    );

    @Operation(summary = "Remover sessao de estudo")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Sessao removida"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Sessao nao encontrada ou ele não é o criador daquela sessao")
    })
    @DeleteMapping("/{idSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSessaoDeEstudo(
        @Parameter(description = "ID da sessao", required = true)
        @PathVariable UUID idSessao,
        HttpServletRequest request
    );

    @Operation(summary = "Listar sessoes do usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping
    List<SessaoDeEstudoResponseDTO> listSessaoDeEstudos(HttpServletRequest request);

   @Operation(summary = "Listar sessoes por grupo")
   @ApiResponses({
       @ApiResponse(responseCode = "200", description = "Lista retornada"),
       @ApiResponse(responseCode = "401", description = "Nao autenticado"),
       @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
   })
   @GetMapping("/byGrupo/{idGrupo}")
   List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByGrupo(
       @Parameter(description = "ID do grupo", required = true)
       @PathVariable UUID idGrupo,
       HttpServletRequest request
   );

   @Operation(summary = "Listar sessoes por disciplina em um grupo")
   @ApiResponses({
       @ApiResponse(responseCode = "200", description = "Lista retornada"),
       @ApiResponse(responseCode = "401", description = "Nao autenticado"),
       @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
   })
   @GetMapping("/bySubject/{disciplina}/grupo/{idGrupo}")
   List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosBySubject(
       @Parameter(description = "Disciplina", required = true)
       @PathVariable String disciplina,
       @Parameter(description = "ID do grupo", required = true)
       @PathVariable UUID idGrupo,
       HttpServletRequest request
   );

   @Operation(summary = "Listar sessoes por topico em um grupo")
   @ApiResponses({
       @ApiResponse(responseCode = "200", description = "Lista retornada"),
       @ApiResponse(responseCode = "401", description = "Nao autenticado"),
       @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
   })
   @GetMapping("/byTopic/{topico}/grupo/{idGrupo}")
   List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByTopic(
       @Parameter(description = "Topico", required = true)
       @PathVariable String topico,
       @Parameter(description = "ID do grupo", required = true)
       @PathVariable UUID idGrupo,
       HttpServletRequest request
   );

    @Operation(summary = "Listar sessoes por disciplina de um usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping("/bySubject/{disciplina}")
    List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByUserAndSubject(
        @Parameter(description = "Disciplina", required = true)
        @PathVariable String disciplina,
        HttpServletRequest request
    );

    @Operation(summary = "Listar sessoes por topico de um usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    @GetMapping("/byTopic/{topico}")
    List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByUserAndTopic(
        @Parameter(description = "Topico", required = true)
        @PathVariable String topico,
        HttpServletRequest request
    );

    @Operation(summary = "Listar sessoes por grupo em ordem cronológica de postagem")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada"),
        @ApiResponse(responseCode = "401", description = "Nao autenticado"),
        @ApiResponse(responseCode = "404", description = "Grupo nao encontrado")
    })
    @GetMapping("/byGrupo/{idGrupo}")
    List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByGroupInOrder(
        @Parameter(description = "ID do grupo", required = true)
        @PathVariable UUID idGrupo,
        HttpServletRequest request
    );
}
