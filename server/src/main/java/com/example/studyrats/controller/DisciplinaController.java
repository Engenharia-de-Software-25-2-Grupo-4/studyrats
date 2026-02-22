package com.example.studyrats.controller;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import com.example.studyrats.service.Disciplina.DisciplinaService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class DisciplinaController implements DisciplinaControllerImpl {

    @Autowired
    private DisciplinaService disciplinaService;

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinas(HttpServletRequest request) {
        getAuthenticatedUserId(request);
        return disciplinaService.listarDisciplinas();
    }

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinasPorUsuario(HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return disciplinaService.listarDisciplinasPorUsuario(idUsuario);
    }

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinasPorGrupo(UUID idGrupo, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return disciplinaService.listarDisciplinasPorGrupo(idGrupo, idUsuario);
    }

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinasPorUsuarioEmGrupo(UUID idGrupo, HttpServletRequest request) {
        String idUsuario = getAuthenticatedUserId(request);
        return disciplinaService.listarDisciplinasPorUsuarioEmGrupo(idUsuario, idGrupo);
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}