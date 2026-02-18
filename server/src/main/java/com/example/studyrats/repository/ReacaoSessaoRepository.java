package com.example.studyrats.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ReacaoSessao;
import java.util.Optional;

public interface ReacaoSessaoRepository extends JpaRepository<ReacaoSessao, UUID> {
    
    Optional<ReacaoSessao> findBySessaoDeEstudo_Id_sessaoAndAutor_FirebaseUid(UUID idSessao, String firebaseUid);

    long countBySessaoDeEstudo_Id_sessao(UUID idSessao);

    boolean existsBySessaoDeEstudo_Id_sessaoAndAutor_FirebaseUid(UUID idSessao, String firebaseUid);

    void deleteBySessaoDeEstudo_Id_sessaoAndAutor_FirebaseUid(UUID idSessao, String firebaseUid);
    
}
