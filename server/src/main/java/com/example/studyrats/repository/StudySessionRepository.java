package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.StudySession;

public interface StudySessionRepository extends JpaRepository<StudySession, UUID> {

    List<StudySession> findByCreatorId(UUID userId); //todas as sessões de um usuário

    List<StudySession> findByGroupId(UUID groupId); //todas as sessões de um grupo 

    List<StudySession> findByGroupIdAndCreatorId(UUID groupId, UUID userId); //todas as sessões de um usuário em um grupo

    List<StudySession> findByGroupIdAndSubject(UUID groupId, String subject); //todas as sessões de um grupo com uma disciplina

    List<StudySession> findByGroupIdAndTopic(UUID groupId, String topic); //todas as sessões de um grupo com um tópico
 
    //find study session by id
    //find study session by user id 
    //find all study sessions
    //find study sessions by subject
    //find study sessions by topic 

}
