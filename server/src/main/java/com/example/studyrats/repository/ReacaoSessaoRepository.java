package com.example.studyrats.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ReacaoSessao;
import java.util.Optional;

public interface ReacaoSessaoRepository extends JpaRepository<ReacaoSessao, UUID> {
    
    Optional<ReacaoSessao> findBySessaoDeEstudoIdSessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    long countBySessaoDeEstudoIdSessao(UUID idSessao);

    boolean existsBySessaoDeEstudoIdSessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    void deleteBySessaoDeEstudoIdSessaoAndAutorFirebaseUid(UUID idSessao, String firebaseUid);

    void deleteByAutor_FirebaseUid(String firebaseUid);

    void deleteBySessaoDeEstudoIdSessao(UUID idSessao);

}
