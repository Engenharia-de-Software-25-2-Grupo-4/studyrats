package com.example.studyrats.service.GrupoDeEstudo;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.GrupoDeEstudo.GrupoDeEstudoResponseDTO;
import com.example.studyrats.dto.ConviteGrupo.ConvitePostRequestDTO;

public interface GrupoDeEstudoService {
    GrupoDeEstudoResponseDTO criarGrupo(GrupoDeEstudoPostPutRequestDTO dto, String uid);
    GrupoDeEstudoResponseDTO buscarPorId(UUID id);
    GrupoDeEstudoResponseDTO atualizar(UUID id, GrupoDeEstudoPostPutRequestDTO dto, String uid);
    void remover(UUID id, String uid);
    List<GrupoDeEstudoResponseDTO> listarPorUsuario(String uid);

    // Convites
    void convidar(UUID idGrupo, ConvitePostRequestDTO dto, String uid);
    void aceitarConvite(UUID idConvite, String uid);
    List<?> listarConvites(String uid);

    // Administração
    void removerCheckinInvalido(UUID idGrupo, java.util.UUID idSessao, String uid);
}
