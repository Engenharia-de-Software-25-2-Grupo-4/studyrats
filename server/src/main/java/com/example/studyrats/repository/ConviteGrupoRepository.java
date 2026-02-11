package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.ConviteGrupo;

public interface ConviteGrupoRepository extends JpaRepository<ConviteGrupo, UUID> {
    List<ConviteGrupo> findByUidConvidado(String uid);
}
