package com.example.studyrats.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ConviteGrupo;

public interface ConviteGrupoRepository extends JpaRepository<ConviteGrupo, UUID> {
    Optional<ConviteGrupo> findByToken(String token);
    void deleteByCriador_FirebaseUid(String firebaseUid);
    void deleteByGrupo_Id(UUID idGrupo);
}
