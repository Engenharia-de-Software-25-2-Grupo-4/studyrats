package com.example.studyrats.controller;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;
import com.example.studyrats.service.Disciplina.DisciplinaService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DisciplinaController implements DisciplinaControllerImpl {

    @Autowired
    private DisciplinaService disciplinaService;

    @Override
    public List<DisciplinaResponseDTO> listarDisciplinas(HttpServletRequest request) {
        getAuthenticatedUserId(request);
        return disciplinaService.listarDisciplinas();
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}