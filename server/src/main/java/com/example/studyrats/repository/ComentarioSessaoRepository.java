package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ComentarioSessao; 
public interface ComentarioSessaoRepository extends JpaRepository<ComentarioSessao, UUID> {
    
    List<ComentarioSessao> findBySessaoDeEstudoIdSessaoOrderByHorarioComentarioAsc(UUID idSessao);

    long countBySessaoDeEstudoIdSessao(UUID idSessao);

    List<ComentarioSessao> findAllBySessaoDeEstudoIdSessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    void deleteByAutor_FirebaseUid(String firebaseUid);

    void deleteBySessaoDeEstudoIdSessao(UUID idSessao);
}
