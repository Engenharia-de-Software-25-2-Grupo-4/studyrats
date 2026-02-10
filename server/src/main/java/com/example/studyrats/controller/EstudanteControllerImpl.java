package com.example.studyrats.controller;

import java.util.List;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.service.estudante.EstudanteService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class EstudanteControllerImpl implements EstudanteController {

    @Autowired
    EstudanteService studentService;

    private String getFirebaseUID(HttpServletRequest request) {
        FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseUser");
        return firebaseToken.getUid();
    }

    public ResponseEntity<EstudanteResponseDTO> criar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request) {
        String uid = getFirebaseUID(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.criar(dto, uid));
    }

    public ResponseEntity<List<EstudanteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(studentService.listarTodos());
    }

    public ResponseEntity<EstudanteResponseDTO> buscarPorId(@PathVariable String firebaseUid) {
        return ResponseEntity.ok(studentService.buscarPorId(firebaseUid));
    }

    public ResponseEntity<EstudanteResponseDTO> atualizar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request) {
        String firebaseUid = getFirebaseUID(request);
        return ResponseEntity.ok(studentService.atualizar(firebaseUid, dto));
    }

    public ResponseEntity<Void> deletar(HttpServletRequest request) {
        String firebaseUid = getFirebaseUID(request);
        studentService.excluir(firebaseUid);
        return ResponseEntity.noContent().build();
    }
    
}
