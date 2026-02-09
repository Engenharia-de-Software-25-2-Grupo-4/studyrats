package com.example.studyrats.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/estudantes")
public interface EstudanteController {

    @PostMapping
    ResponseEntity<EstudanteResponseDTO> criar(@RequestBody EstudantePostPutRequestDTO dto, HttpServletRequest request);

    @GetMapping
    ResponseEntity<List<EstudanteResponseDTO>> listarTodos();

    @GetMapping("/{id}")
    ResponseEntity<EstudanteResponseDTO> buscarPorId(@PathVariable String firebaseUid);

    @PutMapping("/{id}")
    ResponseEntity<EstudanteResponseDTO> atualizar(@PathVariable String firebaseUid, @RequestBody EstudantePostPutRequestDTO dto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletar(@PathVariable String firebaseUid);

}