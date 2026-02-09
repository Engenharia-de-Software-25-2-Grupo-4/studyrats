package com.example.studyrats.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;
import com.example.studyrats.service.estudante.EstudanteService;

import jakarta.servlet.http.HttpServletRequest;

public class EstudanteControllerImpl implements EstudanteController {

 @Autowired
    EstudanteService studentService;

    @PostMapping
    public ResponseEntity<EstudanteResponseDTO> criar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EstudanteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(studentService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudanteResponseDTO> buscarPorId(@PathVariable String firebaseUid) {
        return ResponseEntity.ok(studentService.buscarPorId(firebaseUid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudanteResponseDTO> atualizar(@PathVariable String firebaseUid, @RequestBody EstudantePostPutRequestDTO dto) {
        return ResponseEntity.ok(studentService.atualizar(firebaseUid, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String firebaseUid) {
        studentService.excluir(firebaseUid);
        return ResponseEntity.noContent().build();
    }
    
}
