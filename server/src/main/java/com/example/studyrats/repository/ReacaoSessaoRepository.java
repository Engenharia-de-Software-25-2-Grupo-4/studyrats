package com.example.studyrats.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ReacaoSessao;
import java.util.Optional;

public interface ReacaoSessaoRepository extends JpaRepository<ReacaoSessao, UUID> {
    
    Optional<ReacaoSessao> findBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    long countBySessaoDeEstudoId_sessao(UUID idSessao);

    boolean existsBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    void deleteBySessaoDeEstudoId_sessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);
    
}