package com.example.studyrats.service.UploadsDeImagem;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.studyrats.repository.EstudanteRepository;
import com.example.studyrats.exceptions.ArquivoNaoEhImagem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.example.studyrats.exceptions.ImagemVazia;
import org.springframework.stereotype.Service;

import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class UploadDeImagensService {

    @Value("${app.upload.dir")
    private String caminhoBase;

    @Autowired
    private EstudanteRepository estudanteRepository;

    public String salvarEstudante(MultipartFile imagem, String firebaseUID) throws IOException {
        if (imagem.isEmpty()) {
            throw new ImagemVazia();
        }

        if (!imagem.getContentType().startsWith("image/")) {
            throw new ArquivoNaoEhImagem();
        }

        String nomeOriginal = imagem.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeDoArquivo = firebaseUID+extensao;
        Path caminhoDoUpload = Paths.get(caminhoBase).toAbsolutePath().normalize();

        if (!Files.exists(caminhoDoUpload)) {
            Files.createDirectories(caminhoDoUpload);
        }
        caminhoDoUpload = caminhoDoUpload.resolve("estudantes");

        if (!Files.exists(caminhoDoUpload)) {
            Files.createDirectories(caminhoDoUpload);
        }

        caminhoDoUpload = caminhoDoUpload.resolve(nomeDoArquivo);
        Files.copy(imagem.getInputStream(), caminhoDoUpload, StandardCopyOption.REPLACE_EXISTING);
        return nomeDoArquivo;
    }
}
