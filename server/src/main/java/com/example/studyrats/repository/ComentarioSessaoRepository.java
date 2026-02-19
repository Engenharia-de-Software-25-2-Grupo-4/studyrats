package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ComentarioSessao; 
public interface ComentarioSessaoRepository extends JpaRepository<ComentarioSessao, UUID> {
    
    List<ComentarioSessao> findBySessaoDeEstudoId_sessaoOrderByHorarioComentarioAsc(UUID idSessao);

    long countBySessaoDeEstudoId_sessao(UUID idSessao);

    List<ComentarioSessao> findAllBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);
}