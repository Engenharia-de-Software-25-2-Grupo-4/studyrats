package com.example.studyrats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
    
}
