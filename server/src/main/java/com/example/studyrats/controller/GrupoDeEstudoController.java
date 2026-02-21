package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.GrupoDeEstudo.MembroGrupoResponseDTO;
import com.example.studyrats.dto.GrupoDeEstudo.RankingGrupoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConviteResponseDTO;
import com.example.studyrats.service.GrupoDeEstudo.GrupoDeEstudoService;
import com.example.studyrats.service.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GrupoDeEstudoController implements GrupoDeEstudoControllerInterface {

    @Autowired
    private GrupoDeEstudoService grupoService;
    
    @Autowired
    private FirebaseService firebaseService;

    @Override
    public GrupoDeEstudoResponseDTO createGrupoDeEstudo(GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.criarGrupo(dto, uid);
    }

    @Override
    public GrupoDeEstudoResponseDTO getGrupoDeEstudo(UUID id, HttpServletRequest request) {
        getAuthenticatedUserId(request);
        return grupoService.buscarPorId(id);
    }



    @Override
    public GrupoDeEstudoResponseDTO updateGrupoDeEstudo(UUID id, GrupoDeEstudoPostPutRequestDTO dto, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.atualizar(id, dto, uid);
    }

    @Override
    public void deleteGrupoDeEstudo(UUID id, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.remover(id, uid);
    }

    @Override
    public List<GrupoDeEstudoResponseDTO> listGrupoDeEstudosByUser(HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.listarPorUsuario(uid);
    }

    @Override
    public List<MembroGrupoResponseDTO> listarMembrosDoGrupo(UUID idGrupo, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.listarMembros(idGrupo, uid);
    }

    @Override
    public List<RankingGrupoResponseDTO> getRanking(UUID idGrupo, HttpServletRequest request) {

        getAuthenticatedUserId(request);

        return grupoService.obterRanking(idGrupo);
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listarSessoesDoGrupo(UUID idGrupo, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.listarSessoesDoGrupo(idGrupo, uid);
    }

    @Override
    public String generateInviteLink(UUID idGrupo, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.gerarConviteLink(idGrupo, uid);
    }

    @Override
    public ConviteResponseDTO validateInvite(String token, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        return grupoService.validarConvite(token, uid);
    }

    @Override
    public void joinGroupViaInvite(String token, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.aceitarConvite(token, uid);
    }

    @Override
    public void removeInvalidCheckin(UUID idGrupo, UUID idSessao, HttpServletRequest request) {
        String uid = getAuthenticatedUserId(request);
        grupoService.removerCheckinInvalido(idGrupo, idSessao, uid);
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}