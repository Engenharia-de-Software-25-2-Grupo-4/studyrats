package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ComentarioSessao; 
public interface ComentarioSessaoRepository extends JpaRepository<ComentarioSessao, UUID> {
    
    List<ComentarioSessao> findBySessaoDeEstudo_Id_sessaoOrderByHorarioComentarioAsc(UUID idSessao);

    Long countBySessaoDeEstudo_Id_sessao(UUID idSessao);

    List<ComentarioSessao> findAllBySessaoDeEstudo_Id_sessaoAndAutor_FirebaseUid(UUID idSessao, String firebaseUid);
}
