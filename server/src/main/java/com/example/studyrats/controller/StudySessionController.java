package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController; 
import org.springframework.http.HttpStatus;

import com.example.studyrats.dto.studySession.StudySessionPostPutRequestDTO;
import com.example.studyrats.dto.studySession.StudySessionResponseDTO;
import com.example.studyrats.service.student.StudentService;
import com.example.studyrats.service.studySession.StudySessionService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType; 

@RestController
@RequestMapping(
    value = "/studySessions",
    produces = MediaType.APPLICATION_JSON_VALUE
)

public class StudySessionController {
    
    @Autowired
    private StudySessionService studySessionService; 

    @Autowired
    StudentService studentService; 

    @PostMapping("/group/{groupId}/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public StudySessionResponseDTO createStudySession(
        @PathVariable UUID groupId,
        @PathVariable UUID userId,
        @RequestBody @Valid StudySessionPostPutRequestDTO studySessionPostPutRequestDTO) {
        
        return studySessionService.criarSessaoDeEstudos(groupId, userId, studySessionPostPutRequestDTO);
    }
 
    @GetMapping("/{sessionId}/user/{userId}")
    public StudySessionResponseDTO getStudySession(
        @PathVariable UUID sessionId,
        @PathVariable UUID userId) { 

        return studySessionService.visualizarSessaoDeEstudosPorId(sessionId, userId);
    }

    @PutMapping("/{sessionId}/user/{userId}") 
    public StudySessionResponseDTO updateStudySession(
        @PathVariable UUID sessionId,
        @PathVariable UUID userId,
        @RequestBody @Valid StudySessionPostPutRequestDTO studySessionPostPutRequestDTO) {  

        return studySessionService.atualizarSessaoDeEstudosPorId(sessionId, userId, studySessionPostPutRequestDTO);
    }

    @DeleteMapping("/{sessionId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void deleteStudySession(
        @PathVariable UUID sessionId,
        @PathVariable UUID userId) {

        studySessionService.removerSessaoDeEstudosPorId(sessionId, userId);
    }

    @GetMapping  
    public List<StudySessionResponseDTO> listStudySessions() { 

        UUID userId = studentService.getAuthenticatedStudentId();
        return studySessionService.listarSessaoDeEstudosPorUsuario(userId);
    }

    @GetMapping("/byGroup/{groupId}/user/{userId}")
    public List<StudySessionResponseDTO> listStudySessionsByGroup(
        @PathVariable UUID groupId,
        @PathVariable UUID userId) {

        return studySessionService.listarSessaoDeEstudosPorGrupo(groupId, userId);
    }

    @GetMapping("/bySubject/{subject}/group/{groupId}/user/{userId}")
    public List<StudySessionResponseDTO> listStudySessionsBySubject(
        @PathVariable String subject,
        @PathVariable UUID groupId,
        @PathVariable UUID userId) {    

        return studySessionService.listarSessaoDeEstudosPorDisciplinaEmGrupo(subject, groupId, userId);
    }

    @GetMapping("/byTopic/{topic}/group/{groupId}/user/{userId}")
    public List<StudySessionResponseDTO> listStudySessionsByTopic(
        @PathVariable String topic,
        @PathVariable UUID groupId,
        @PathVariable UUID userId) { 

        return studySessionService.listarSessaoDeEstudosPorTopicoEmGrupo(topic, groupId, userId); 
    }
}

