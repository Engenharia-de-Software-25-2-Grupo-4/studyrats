package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConvitePostRequestDTO;
import com.example.studyrats.service.GrupoDeEstudo.GrupoDeEstudoService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GrupoDeEstudoController implements GrupoDeEstudoControllerInterface {

    @Autowired
    private GrupoDeEstudoService grupoService;

    @Override
    public GrupoDeEstudoResponseDTO criarGrupo(GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.criarGrupo(dto, uid);
    }

    @Override
    public GrupoDeEstudoResponseDTO buscarPorId(UUID id) {
        return grupoService.buscarPorId(id);
    }

    @Override
    public GrupoDeEstudoResponseDTO atualizar(UUID id, GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.atualizar(id, dto, uid);
    }

    @Override
    public void remover(UUID id, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.remover(id, uid);
    }

    @Override
    public List<GrupoDeEstudoResponseDTO> listarPorUsuario(HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.listarPorUsuario(uid);
    }

    @Override
    public void convidar(UUID id, ConvitePostRequestDTO dto, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.convidar(id, dto, uid);
    }

    @Override
    public void aceitarConvite(UUID idConvite, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.aceitarConvite(idConvite, uid);
    }

    @Override
    public List<?> listarConvites(HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.listarConvites(uid);
    }

    @Override
    public void removerCheckinInvalido(UUID idGrupo, UUID idSessao, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.removerCheckinInvalido(idGrupo, idSessao, uid);
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}
