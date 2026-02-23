package com.example.studyrats.service.Interacao;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoPostPutRequestDTO;
import com.example.studyrats.dto.Interacao.Comentario.ComentarioSessaoResponseDTO;
import com.example.studyrats.dto.Interacao.Reacao.ReacaoSessaoResponseDTO;

public interface InteracaoService {

    ComentarioSessaoResponseDTO criarComentarioSessao(UUID idSessao, ComentarioSessaoPostPutRequestDTO comentarioSessaoPostPutRequestDTO, String firebaseUid);

    List<ComentarioSessaoResponseDTO> listarComentariosSessao(UUID idSessao, String firebaseUid);

    void deletarComentarioSessao(UUID idComentario, String firebaseUid);

    ReacaoSessaoResponseDTO reagirOuTirarReacaoSessao(UUID idSessao, String firebaseUid);

    ReacaoSessaoResponseDTO listarReacoesSessao(UUID idSessao, String firebaseUid); 

}
