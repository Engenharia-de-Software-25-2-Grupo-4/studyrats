package com.example.studyrats.service.studySession;

import org.springframework.stereotype.Service;

import com.example.studyrats.dto.studySession.StudySessionPostPutRequestDTO;
import com.example.studyrats.dto.studySession.StudySessionResponseDTO;
import com.example.studyrats.exceptions.StudySessionNotFoundException;
import com.example.studyrats.model.Student;
import com.example.studyrats.model.StudySession;
import com.example.studyrats.repository.StudentRepository;
import com.example.studyrats.repository.StudySessionRepository; 
import com.example.studyrats.exceptions.AccessDeniedException;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.modelmapper.ModelMapper;

@Service
@Transactional
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
        Student student = studentRepository.findById(userId).orElseThrow(() -> new RuntimeException("Student not found")); 
        //quando tiver crud de grupo, validar se o grupo existe e se o usuário faz parte dele 

        StudySession studySession = modelMapper.map(studySessionPostPutRequestDTO, StudySession.class);
        studySession.setCreator(student);
        studySession.setGroupId(groupId); 

        studySessionRepository.save(studySession);
        return modelMapper.map(studySession, StudySessionResponseDTO.class);

    }

    @Override
    public StudySessionResponseDTO visualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new); 
        //quando tiver crud de grupo, validar se o grupo existe e se o usuário faz parte dele 
        validateCreator(studySession, userId); 

        return modelMapper.map(studySession, StudySessionResponseDTO.class);
    }

    @Override
    public void removerSessaoDeEstudosPorId(UUID sessionId, UUID userId) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new); 
        validateCreator(studySession, userId);

        studySessionRepository.delete(studySession); 
    }

    @Override
    public StudySessionResponseDTO atualizarSessaoDeEstudosPorId(UUID sessionId, UUID userId, StudySessionPostPutRequestDTO studySessionPostPutRequestDTO) {
        StudySession studySession = studySessionRepository.findById(sessionId).orElseThrow(StudySessionNotFoundException::new); 
        validateCreator(studySession, userId);

        modelMapper.map(studySessionPostPutRequestDTO, studySession);
        studySessionRepository.save(studySession);
        return modelMapper.map(studySession, StudySessionResponseDTO.class);
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuario(UUID userId) {
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByCreator_Id(userId)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorUsuarioEmGrupo(UUID userId, UUID groupId) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndCreator_Id(groupId, userId)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorDisciplinaEmGrupo(String subject, UUID groupId, UUID userId) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndSubject(groupId, subject)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions; 
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorTopicoEmGrupo(String topic, UUID groupId, UUID userId) {
        // verificar se usuário é do grupo quando tiver crud de grupo
        List<StudySessionResponseDTO> sessions = studySessionRepository.findByGroupIdAndTopic(groupId, topic)
            .stream()
            .map(session -> modelMapper.map(session, StudySessionResponseDTO.class))
            .toList();
        return sessions;
    }

    @Override
    public List<StudySessionResponseDTO> listarSessaoDeEstudosPorGrupo(UUID groupId, UUID userId) { 
        // verificar se usuário é do grupo quando tiver crud de grupo
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
    
    private void validateCreator(StudySession studySession, UUID userId) {
        if (!studySession.getCreator().getId().equals(userId) || studySession.getCreator() == null) {
            throw new AccessDeniedException();
        }
    }
}
