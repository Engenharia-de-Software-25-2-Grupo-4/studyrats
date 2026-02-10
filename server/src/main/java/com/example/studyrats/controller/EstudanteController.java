package com.example.studyrats.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Estudante", description = "Operações de gerenciamento de perfil e conta do estudante")
@RequestMapping("/estudantes")
public interface EstudanteController {

    @Operation(summary = "Criar um novo estudante", description = "Cadastra um estudante vinculado ao UID do Firebase fornecido no token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Estudante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
        @ApiResponse(responseCode = "409", description = "Este e-mail/Firebase_UID já está vinculado a uma conta ativa.")
    })
    @PostMapping
    ResponseEntity<EstudanteResponseDTO> criar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request);

    @Operation(summary = "Listar todos os estudantes", description = "Retorna uma lista de todos os estudantes cadastrados (Uso administrativo).")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    ResponseEntity<List<EstudanteResponseDTO>> listarTodos();

    @Operation(summary = "Buscar estudante por ID", description = "Recupera os dados detalhados de um estudante através do seu Firebase UID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante encontrado"),
        @ApiResponse(responseCode = "404", description = "O estudante solicitado não foi encontrado no sistema.")
    })
    @GetMapping("/{firebaseUid}")
    ResponseEntity<EstudanteResponseDTO> buscarPorId(@PathVariable String firebaseUid);

    @Operation(summary = "Atualizar dados do estudante", description = "Atualiza as informações de perfil do estudante.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estudante atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "O estudante solicitado não foi encontrado no sistema.")
    })
    @PutMapping
    ResponseEntity<EstudanteResponseDTO> atualizar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request);

    @Operation(summary = "Deletar conta do estudante", description = "Remove permanentemente os dados do estudante (Respeitando a LGPD).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Estudante removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "O estudante solicitado não foi encontrado no sistema.")
    })
    @DeleteMapping
    ResponseEntity<Void> deletar(HttpServletRequest request);

}