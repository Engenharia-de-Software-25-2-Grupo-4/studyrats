package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.GrupoDeEstudo;

public interface GrupoDeEstudoRepository extends JpaRepository<GrupoDeEstudo, UUID> {
    List<GrupoDeEstudo> findByMembros_Estudante_FirebaseUid(String firebaseUid);
}
