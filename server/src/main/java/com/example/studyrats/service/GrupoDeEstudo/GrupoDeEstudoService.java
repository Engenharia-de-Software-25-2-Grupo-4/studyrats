package com.example.studyrats.service.GrupoDeEstudo;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConviteResponseDTO;

public interface GrupoDeEstudoService {

    GrupoDeEstudoResponseDTO criarGrupo(GrupoDeEstudoPostPutRequestDTO dto, String uid);

    GrupoDeEstudoResponseDTO buscarPorId(UUID id);

    GrupoDeEstudoResponseDTO atualizar(UUID id, GrupoDeEstudoPostPutRequestDTO dto, String uid);

    void remover(UUID id, String uid);

    List<GrupoDeEstudoResponseDTO> listarPorUsuario(String uid);

    // Convites
    String gerarConviteLink(UUID idGrupo, String uidAdmin);

    void aceitarConvite(String token, String uidUsuario);

    ConviteResponseDTO validarConvite(String token, String uidUsuario);

    // Administração
    void removerCheckinInvalido(UUID idGrupo, UUID idSessao, String uid);
}