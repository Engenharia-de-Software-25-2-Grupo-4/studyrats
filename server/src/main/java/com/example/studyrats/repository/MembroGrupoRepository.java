package com.example.studyrats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.MembroGrupo;

public interface MembroGrupoRepository extends JpaRepository<MembroGrupo, Long> {
    Optional<MembroGrupo> findByGrupo_IdAndEstudante_FirebaseUid(java.util.UUID grupoId, String firebaseUid);
    boolean existsByGrupo_IdAndEstudante_FirebaseUid(java.util.UUID grupoId, String firebaseUid);
}
