package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.SessaoDeEstudo;

public interface SessaoDeEstudoRepository extends JpaRepository<SessaoDeEstudo, UUID> {

    List<SessaoDeEstudo> findByCriador_FirebaseUid(String id_usuario); //todas as sessões de um usuário

    List<SessaoDeEstudo> findByCriador_FirebaseUidAndDisciplina(String id_usuario, String disciplina); //todas as sessões de um usuário com uma disciplina

    List<SessaoDeEstudo> findByCriador_FirebaseUidAndTopico(String id_usuario, String topico); //todas as sessões de um usuário com um tópico

    List<SessaoDeEstudo> findByIdGrupo(UUID id_grupo); //todas as sessões de um grupo 

    List<SessaoDeEstudo> findByIdGrupoAndCriador_FirebaseUid(UUID id_grupo, String id_usuario); //todas as sessões de um usuário em um grupo
    
    List<SessaoDeEstudo> findByIdGrupoAndDisciplina(UUID id_grupo, String disciplina); //todas as sessões de um grupo com uma disciplina

    List<SessaoDeEstudo> findByIdGrupoAndTopico(UUID id_grupo, String topico); //todas as sessões de um grupo com um tópico

}
