package com.example.studyrats.service.studySession;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.studySession.StudySessionPostPutRequestDTO;
import com.example.studyrats.dto.studySession.StudySessionResponseDTO;

public interface StudySessionService {
    //create, list by group, get by id, update, delete, list by user, list by subject, list by topic 

    //o grupo existe? o user é membro do grupo? user pode editar? start time é válido? tem foto? user existe? tem título? 

    StudySessionResponseDTO criarSessaoDeEstudos(UUID groupId, UUID userId, StudySessionPostPutRequestDTO studySessionPostPutRequestDTO);

    StudySessionResponseDTO visualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId); 

    void removerSessaoDeEstudosPorId(UUID sessionId, UUID userId);

    StudySessionResponseDTO atualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId, StudySessionPostPutRequestDTO studySessionPostPutRequestDTO); 

    List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuario(UUID userId); //listar todas de um usuário em todos os grupos dele

    List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(UUID userId, UUID groupId); //listar todas de um usuário de um grupo 

    List<StudySessionResponseDTO> listarSessaoDeEstudosPorDisciplina(String subject, UUID groupId, UUID userId); //listar todas as sessoes de um grupo que tenham essa disciplina

    List<StudySessionResponseDTO> listarSessaoDeEstudosPorTopico(String topic, UUID groupId, UUID userId); //listar todas de um grupo que tenham esse tópico 
    
    List<StudySessionResponseDTO> listarSessaoDeEstudosPorGrupo(UUID groupId, UUID userId); //listar todas de um grupo
}

