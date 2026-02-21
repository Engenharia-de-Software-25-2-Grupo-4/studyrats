package com.example.studyrats.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Uplaod de imagens", description = "Operações para salvar e recuperar imagens do estudantes, grupos de estudos e sessões de etudo")
@RequestMapping("/imagens")
public interface UploadDeImagensController {

    @PostMapping("/upload/estudante")
    ResponseEntity<?> adicionarImagemEstudante(@RequestParam("imagem") MultipartFile imagem, HttpServletRequest request) throws IOException;

    @GetMapping("/estudante/{firebaseUID}")
    ResponseEntity<?> retornarImagemEstudante(@PathVariable String firebaseUID, HttpServletRequest request);

    @PostMapping("/upload/sessaoDeEstudo/{idSessaoDeEstudo}")
    ResponseEntity<?> adicionarImagemSessaoDeEstudo(@RequestParam("imagem") MultipartFile imagem, @PathVariable UUID idSessaoDeEstudo, HttpServletRequest request) throws IOException;

    @GetMapping("/sessaoDeEstudo/{idSessaoDeEstudo}")
    ResponseEntity<?> retornarImagemSessaoDeEstudo(@PathVariable String idSessaoDeEstudo);

    @PostMapping("/upload/grupo/{idGrupo}")
    ResponseEntity<?> adicionarImagemGrupoDeEstudo(@PathVariable String idGrupo, @RequestParam("imagem") MultipartFile imagem, HttpServletRequest request) throws IOException;

    @GetMapping("/grupo/{idGrupo}")
    ResponseEntity<?> retornarImagemGrupoDeEstudo(@PathVariable String idGrupo, HttpServletRequest request);
}
