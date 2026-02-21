package com.example.studyrats.controller;

import com.example.studyrats.dto.Topico.TopicoResponseDTO;
import com.example.studyrats.service.Topico.TopicoService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopicoController implements TopicoControllerImpl {

    @Autowired
    private TopicoService topicoService;

    @Override
    public List<TopicoResponseDTO> listarTopicos(HttpServletRequest request) {
        getAuthenticatedUserId(request);
        return topicoService.listarTopicos();
    }

    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        return ((FirebaseToken) firebaseUser).getUid();
    }
}