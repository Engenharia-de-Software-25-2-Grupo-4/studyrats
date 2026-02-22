package com.example.studyrats.controller;

import com.example.studyrats.service.UploadsDeImagem.UploadDeImagensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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
import java.util.Optional;
import java.util.UUID;

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
    private ResponseEntity<?> processarERetornarResource(Path caminhoDaImagem, String id) throws IOException {
        Resource resource;

        Optional<Path> arquivo = Files.list(caminhoDaImagem)
                .filter(p -> p.getFileName().toString().startsWith(id + "."))
                .findFirst();

        if (arquivo.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path caminho = arquivo.get();
        resource = new UrlResource(caminho.toUri());
        String nome = caminho.getFileName().toString().toLowerCase();

        String contentType;
        if (nome.endsWith(".png")) {
            contentType = "image/png";
        } else if (nome.endsWith(".jpg") || nome.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (nome.endsWith(".webp")) {
            contentType = "image/webp";
        } else {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    @Override
    public ResponseEntity<?> adicionarImagemEstudante(MultipartFile imagem, HttpServletRequest request) throws IOException {
        String firebaseUID = getFirebaseUID(request);
        return ResponseEntity.ok(uploadDeImagensService.salvarEstudante(imagem, firebaseUID));
    }

    @Override
    public ResponseEntity<?> retornarImagemEstudante(String firebaseUID, HttpServletRequest request) throws IOException {
        Path caminhoDaImagem = Paths.get(caminhoBase, "estudantes").toAbsolutePath().normalize();
        return processarERetornarResource(caminhoDaImagem, firebaseUID);
    }

    @Override
    public ResponseEntity<?> adicionarImagemGrupoDeEstudo(String idGrupo, MultipartFile imagem, HttpServletRequest request) throws IOException {
        String firebaseUID = getFirebaseUID(request);
        String nomeArquivoSalvo = uploadDeImagensService.salvarGrupoDeEstudo(imagem, idGrupo, firebaseUID);

        return ResponseEntity.ok(nomeArquivoSalvo);
    }

    @Override
    public ResponseEntity<?> retornarImagemGrupoDeEstudo(String idGrupo, HttpServletRequest request) throws IOException {
        Path caminhoDaImagem = Paths.get(caminhoBase, "gruposDeEstudo").toAbsolutePath().normalize();
        return processarERetornarResource(caminhoDaImagem, idGrupo);
    }

    @Override
    public ResponseEntity<?> adicionarImagemSessaoDeEstudo(MultipartFile imagem, UUID idSessaoDeEstudo, HttpServletRequest request) throws IOException {
        String firebaseUID = getFirebaseUID(request);
        return ResponseEntity.ok(uploadDeImagensService.salvarSessaoDeEstudo(imagem, idSessaoDeEstudo, firebaseUID));
    }

    @Override
    public ResponseEntity<?> retornarImagemSessaoDeEstudo(String idSessaoDeEstudo) throws IOException {
        Path caminhoDaImagem = Paths.get(caminhoBase, "sessoesDeEstudo").toAbsolutePath().normalize();
        return processarERetornarResource(caminhoDaImagem, idSessaoDeEstudo);
    }


}
