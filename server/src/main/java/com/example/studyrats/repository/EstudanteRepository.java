package com.example.studyrats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.Estudante;

public interface EstudanteRepository extends JpaRepository<Estudante, String> {
    
    boolean existsByEmail(String email);
    Optional<Estudante> findByEmail(String email);
}