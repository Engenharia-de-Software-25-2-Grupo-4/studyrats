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

    @Value("${app.upload.dir}")
    private String caminhoBase;

    @Autowired
    private EstudanteRepository estudanteRepository;

    private record PathRecord(Path caminhoDoUpload, String nomeDoArquivo) {}
    private PathRecord gerarPathAPartirDaImagem(MultipartFile imagem, String idDoArquivo) throws IOException {
        if (imagem.isEmpty()) {
            throw new ImagemVazia();
        }

        if (!imagem.getContentType().startsWith("image/")) {
            throw new ArquivoNaoEhImagem();
        }

        String nomeOriginal = imagem.getOriginalFilename();
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String nomeDoArquivo = idDoArquivo+extensao;
        Path caminhoDoUpload = Paths.get(caminhoBase).toAbsolutePath().normalize();

        if (!Files.exists(caminhoDoUpload)) {
            Files.createDirectories(caminhoDoUpload);
        }
        return new PathRecord(caminhoDoUpload, nomeDoArquivo);
    }

    private Path extenderPathEstudante(Path caminhoDoUpload) throws IOException {
        return extenderPath(caminhoDoUpload, "estudantes");
    }

    private Path extentenderPathGrupoDeEstudo(Path caminhoDoUpload) throws IOException {
        return extenderPath(caminhoDoUpload, "gruposDeEstudo");
    }

    private Path extenderPathSessaoDeEstudo(Path caminhoDoUpload) throws IOException {
        return extenderPath(caminhoDoUpload, "sessoesDeEstudo");
    }

    private Path extenderPath(Path caminhoDoUpload, String extensao) throws IOException {
        caminhoDoUpload = caminhoDoUpload.resolve(extensao);

        if (!Files.exists(caminhoDoUpload)) {
            Files.createDirectories(caminhoDoUpload);
        }

        return caminhoDoUpload;
    }

    public String salvarEstudante(MultipartFile imagem, String firebaseUID) throws IOException {
        PathRecord caminhoDoUploadENome = gerarPathAPartirDaImagem(imagem, firebaseUID);
        String nomeDoArquivo = caminhoDoUploadENome.nomeDoArquivo;
        Path caminhoDoUpload = extenderPathEstudante(caminhoDoUploadENome.caminhoDoUpload);
        caminhoDoUpload = caminhoDoUpload.resolve(nomeDoArquivo);
        Files.copy(imagem.getInputStream(), caminhoDoUpload, StandardCopyOption.REPLACE_EXISTING);
        return nomeDoArquivo;
    }
}
