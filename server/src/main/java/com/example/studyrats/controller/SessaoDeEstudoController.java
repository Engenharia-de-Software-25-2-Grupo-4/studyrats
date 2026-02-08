package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.http.HttpStatus;

import com.example.studyrats.dto.SessaoDeEstudo.*;
import com.example.studyrats.exceptions.AccessDeniedException;
import com.example.studyrats.service.SessaoDeEstudo.SessaoDeEstudoService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType; 

@RestController
@RequestMapping(
    value = "/sessaoDeEstudo",
    produces = MediaType.APPLICATION_JSON_VALUE
)


public class SessaoDeEstudoController {
    
    @Autowired
    private SessaoDeEstudoService sessaoDeEstudoService; 

    @PostMapping("/grupo/{idGrupo}")
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoDeEstudoResponseDTO createSessaoDeEstudo(
        @PathVariable UUID idGrupo,
        @RequestBody @Valid SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO,
        HttpServletRequest request) {
        
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.criarSessaoDeEstudos(idGrupo, idUsuario, sessaoDeEstudoPostPutRequestDTO);
    }
 
    @GetMapping("/{idSessao}")
    public SessaoDeEstudoResponseDTO getSessaoDeEstudo(
        @PathVariable UUID idSessao,
        HttpServletRequest request) { 

        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.visualizarSessaoDeEstudosPorId(idSessao, idUsuario);
    }

    @PutMapping("/{idSessao}") 
    public SessaoDeEstudoResponseDTO updateSessaoDeEstudo(
        @PathVariable UUID idSessao,
        @RequestBody @Valid SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO,
        HttpServletRequest request) {  

        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.atualizarSessaoDeEstudosPorId(idSessao, idUsuario, sessaoDeEstudoPostPutRequestDTO);
    }

    @DeleteMapping("/{idSessao}")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void deleteSessaoDeEstudo(
        @PathVariable UUID idSessao,
        HttpServletRequest request) {

        String idUsuario = getAuthenticatedUserId(request);
        sessaoDeEstudoService.removerSessaoDeEstudosPorId(idSessao, idUsuario);
    }

    @GetMapping  
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudos(HttpServletRequest request) { 
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorUsuario(idUsuario);
    }

    @GetMapping("/byGrupo/{idGrupo}")
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByGrupo(
        @PathVariable UUID idGrupo,
        HttpServletRequest request) {

        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorGrupo(idGrupo, idUsuario);
    }

    @GetMapping("/bySubject/{disciplina}/grupo/{idGrupo}")
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosBySubject(
        @PathVariable String disciplina,
        @PathVariable UUID idGrupo,
        HttpServletRequest request) {    

        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorDisciplinaEmGrupo(disciplina, idGrupo, idUsuario);
    }

    @GetMapping("/byTopic/{topico}/grupo/{idGrupo}")
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByTopic(
        @PathVariable String topico,
        @PathVariable UUID idGrupo,
        HttpServletRequest request) { 

        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorTopicoEmGrupo(topico, idGrupo, idUsuario); 
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        if (firebaseUser == null) {
            throw new AccessDeniedException();
        }
        return ((FirebaseToken) firebaseUser).getUid();
    }
}
