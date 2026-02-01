package com.example.studyrats.service.studySession;

import org.springframework.stereotype.Service;

import com.example.studyrats.dto.studySession.StudySessionPostPutRequestDTO;
import com.example.studyrats.dto.studySession.StudySessionResponseDTO;
import com.example.studyrats.model.StudySession;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.repository.StudySessionRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;

@Service
public class StudySessionServiceImpl implements StudySessionService {

    @Autowired
    public StudySessionRepository studySessionRepository; 
    @Autowired
    public StudentRepository studentRepository; 
    // @Autowired 
    // public StudyGroupRepository studyGroupRepository;
    @Autowired
    public ModelMapper modelMapper; 

    @Override
    public StudySessionResponseDTO criarSessaoDeEstudos(UUID groupId, UUID userId, StudySessionPostPutRequestDTO studySessionPostPutRequestDTO) {
        StudySession studySession = modelMapper.map(studySessionPostPutRequestDTO, StudySession.class);
        studySessionRepository.save(studySession);
        return modelMapper.map(studySession, StudySessionResponseDTO.class);

    }

    @Override
    public StudySessionResponseDTO visualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new);
        return modelMapper.map(studySession, StudySessionResponseDTO.class);
    }

    @Override
    public void removerSessaoDeEstudosPorId(UUID sessionId, UUID userId) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new);
        studySessionRepository.delete(studySession);
    }

    @Override
    public StudySessionResponseDTO atualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId, StudySessionPostPutRequestDTO studySessionPostPutRequestDTO) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new);
        modelMapper.map(studySessionPostPutRequestDTO, studySession);
        studySessionRepository.save(studySession);
        return modelMapper.map(studySession, StudySessionResponseDTO.class);
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuario(UUID userId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByCreatorId(userId)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(UUID userId, UUID groupId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndCreatorId(groupId, userId)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorDisciplina(String subject, UUID groupId, UUID userId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndSubject(groupId, subject)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions; 
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorTopico(String topic, UUID groupId, UUID userId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndTopic(groupId, topic)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorGrupo(UUID groupId, UUID userId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupId(groupId)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    } 

    // private StudySessionResponseDTO toDTO(StudySession session) {
    // return StudySessionResponseDTO.builder()
    //     .sessionId(session.getSessionId())
    //     .creatorId(session.getCreator().getId())  
    //     .creatorName(session.getCreator().getName())
    //     .title(session.getTitle())
    //     .build();
    // }
    
}
