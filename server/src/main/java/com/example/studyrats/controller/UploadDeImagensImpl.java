package com.example.studyrats.controller;

import com.example.studyrats.service.UploadsDeImagem.UploadDeImagensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.google.firebase.auth.FirebaseToken;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class UploadDeImagensImpl implements UploadDeImagensController {

    @Autowired
    private UploadDeImagensService uploadDeImagensService;
    @Value("${app.upload.dir}")
    private String caminhoBase;

    private String getFirebaseUID(HttpServletRequest request) {
        FirebaseToken firebaseToken = (FirebaseToken) request.getAttribute("firebaseUser");
        return firebaseToken.getUid();
    }

    @Override
    public ResponseEntity<?> adicionarImagemEstudante(MultipartFile imagem, HttpServletRequest request) throws IOException {
        String firebaseUID = getFirebaseUID(request);
        return ResponseEntity.ok(uploadDeImagensService.salvarEstudante(imagem, firebaseUID));
    }

    @Override
    public ResponseEntity<?> retornarImagemEstudante(String firebaseUID, HttpServletRequest request) {
        Path caminhoDaImagem = Paths.get(caminhoBase, "estudantes", firebaseUID).toAbsolutePath().normalize();
        Resource resource;
        try {
            resource = new UrlResource(caminhoDaImagem.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        String contentType;
        try {
            contentType = Files.probeContentType(caminhoDaImagem);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @Override
    public ResponseEntity<?> adicionarImagemGrupoDeEstudo(String idGrupo, MultipartFile imagem, HttpServletRequest request) throws IOException {
        String firebaseUID = getFirebaseUID(request);
        String nomeArquivoSalvo = uploadDeImagensService.salvarGrupoDeEstudo(imagem, idGrupo, firebaseUID);

        return ResponseEntity.ok(nomeArquivoSalvo);
    }

    @Override
    public ResponseEntity<?> retornarImagemGrupoDeEstudo(String idGrupo, HttpServletRequest request) {
        Path caminhoDaImagem = Paths.get(caminhoBase, "gruposDeEstudo", idGrupo).toAbsolutePath().normalize();
        Resource resource;
        try {
            resource = new UrlResource(caminhoDaImagem.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao ler o arquivo", e);
        }

        String contentType;
        try {
            contentType = Files.probeContentType(caminhoDaImagem);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
