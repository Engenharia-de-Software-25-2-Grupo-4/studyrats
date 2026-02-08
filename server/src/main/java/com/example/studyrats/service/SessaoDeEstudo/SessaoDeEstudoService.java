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

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuario(String idUsuario);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(String idUsuario, UUID idGrupo);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String disciplina, UUID idGrupo, String idUsuario);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topico, UUID idGrupo, String idUsuario);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosPorGrupo(UUID idGrupo, String idUsuario);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorDisciplina(String idUsuario, String disciplina);

    List<SessaoDeEstudoResponseDTO> listarSessaoDeEstudosDeUsuarioPorTopico(String idUsuario, String topico);

}

