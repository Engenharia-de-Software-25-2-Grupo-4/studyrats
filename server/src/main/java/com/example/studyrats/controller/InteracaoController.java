// src/main/java/com/example/studyrats/controller/InteracaoController.java
package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoPostPutRequestDTO;
import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoResponseDTO;
import com.example.studyrats.dto.Interacao.Reacao.ReacaoSessaoResponseDTO;
import com.example.studyrats.service.Interacao.InteracaoService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class InteracaoController implements InteracaoControllerInterface {

    @Autowired
    private InteracaoService reacaoService;

    @Override
    public ComentarioSessaoResponseDTO criarComentarioSessao(UUID idSessao,
            @Valid @org.springframework.web.bind.annotation.RequestBody ComentarioSessaoPostPutRequestDTO dto,
            HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return reacaoService.criarComentarioSessao(idSessao, dto, uid);
    }

    @Override
    public List<ComentarioSessaoResponseDTO> listarComentariosSessao(UUID idSessao, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return reacaoService.listarComentariosSessao(idSessao, uid);
    }

    @Override
    public void deletarComentarioSessao(UUID idComentario, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        reacaoService.deletarComentarioSessao(idComentario, uid);
    }

    @Override
    public ReacaoSessaoResponseDTO reagirOuTirarReacaoSessao(UUID idSessao, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return reacaoService.reagirOuTirarReacaoSessao(idSessao, uid);
    }

    @Override
    public ReacaoSessaoResponseDTO listarReacoesSessao(UUID idSessao, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return reacaoService.listarReacoesSessao(idSessao, uid);
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}
