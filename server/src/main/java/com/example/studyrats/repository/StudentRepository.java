package com.example.studyrats.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    
    Optional<Student> findByEmail(String email);
}