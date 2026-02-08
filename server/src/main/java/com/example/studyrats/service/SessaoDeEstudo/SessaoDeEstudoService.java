package com.example.studyrats.service.SessaoDeEstudo;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoPostPutRequestDTO;
import com.example.studyrats.dto.SessaoDeEstudo.SessaoDeEstudoResponseDTO;

public interface SessaoDeEstudoService {

    SessaoDeEstudoResponseDTO criarSessaoDeEstudos(UUID idGrupo, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO);

    SessaoDeEstudoResponseDTO visualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario); 

    void removerSessaoDeEstudosPorId(UUID idSessao, String idUsuario);

    SessaoDeEstudoResponseDTO atualizarSessaoDeEstudosPorId(UUID idSessao, String idUsuario, SessaoDeEstudoPostPutRequestDTO sessaoDeEstudoPostPutRequestDTO); 

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuario(String idUsuario); //listar todas de um usuário em todos os grupos dele

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(String idUsuario, UUID idGrupo); //listar todas de um usuário de um grupo 

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String disciplina, UUID idGrupo, String idUsuario); //listar todas as sessoes de um grupo que tenham essa disciplina

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topico, UUID idGrupo, String idUsuario); //listar todas de um grupo que tenham esse tópico

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupo(UUID idGrupo, String idUsuario); //listar todas de um grupo

}
