package com.example.studyrats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.studyrats.model.Disciplina;
import java.util.Optional;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Optional<Disciplina> findByNome(String nome);

    boolean existsByNome(String nome);
}

