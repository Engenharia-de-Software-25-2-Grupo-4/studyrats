package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController; 

import com.example.studyrats.dto.SessaoDeEstudo.*;
import com.example.studyrats.service.SessaoDeEstudo.SessaoDeEstudoService;
import com.google.firebase.auth.FirebaseToken;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SessaoDeEstudoController implements SessaoDeEstudoControllerInterface {
    
    @Autowired
    private SessaoDeEstudoService sessaoDeEstudoService; 

    @Override
    public SessaoDeEstudoResponseDTO createSessaoDeEstudo(UUID idGrupo, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.criarSessaoDeEstudos(idGrupo, idUsuario, sessaoDeEstudoPostPutRequestDTO);
    }
 
    @Override
    public SessaoDeEstudoResponseDTO getSessaoDeEstudo(UUID idSessao, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.visualizarSessaoDeEstudosPorId(idSessao, idUsuario);
    }

    @Override
    public SessaoDeEstudoResponseDTO updateSessaoDeEstudo(UUID idSessao, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.atualizarSessaoDeEstudosPorId(idSessao, idUsuario, sessaoDeEstudoPostPutRequestDTO);
    }

    @Override
    public void deleteSessaoDeEstudo(UUID idSessao, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        sessaoDeEstudoService.removerSessaoDeEstudosPorId(idSessao, idUsuario);
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudos(HttpServletRequest request) { 
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorUsuario(idUsuario);
    }

   @Override
   public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByGrupo(UUID idGrupo, HttpServletRequest request) {
       String idUsuario = getAuthenticatedUserId(request);
       return sessaoDeEstudoService.listarSessaoDeEstudosPorGrupo(idGrupo, idUsuario);
   }

   @Override
   public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosBySubject(String disciplina, UUID idGrupo, HttpServletRequest request) {
       String idUsuario = getAuthenticatedUserId(request);
       return sessaoDeEstudoService.listarSessaoDeEstudosPorDisciplinaEmGrupo(disciplina, idGrupo, idUsuario);
   }

   @Override
   public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByTopic(String topico, UUID idGrupo, HttpServletRequest request) {
       String idUsuario = getAuthenticatedUserId(request);
       return sessaoDeEstudoService.listarSessaoDeEstudosPorTopicoEmGrupo(topico, idGrupo, idUsuario);
   }

    @Override
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByUserAndSubject(String disciplina, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosDeUsuarioPorDisciplina(idUsuario, disciplina);
    }

    @Override
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByUserAndTopic(String topico, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosDeUsuarioPorTopico(idUsuario, topico);
    } 

    @Override
    public List<SessaoDeEstudoResponseDTO> listSessaoDeEstudosByGroupInOrder(UUID idGrupo, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return sessaoDeEstudoService.listarSessaoDeEstudosPorGrupoCronologicamente(idGrupo, idUsuario);
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}
