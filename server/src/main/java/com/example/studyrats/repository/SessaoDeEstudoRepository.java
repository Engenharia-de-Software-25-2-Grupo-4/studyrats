package com.example.studyrats.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.studyrats.model.SessaoDeEstudo;

public interface SessaoDeEstudoRepository extends JpaRepository<SessaoDeEstudo, UUID> {

    List<SessaoDeEstudo> findByCriador_FirebaseUid(String id_usuario);

    List<SessaoDeEstudo> findByCriador_FirebaseUidAndDisciplina(String id_usuario, String disciplina); 

    List<SessaoDeEstudo> findByCriador_FirebaseUidAndTopico(String id_usuario, String topico);

    List<SessaoDeEstudo> findByGrupoDeEstudo_Id(UUID id_grupo);

    List<SessaoDeEstudo> findByGrupoDeEstudo_IdAndCriador_FirebaseUid(UUID id_grupo, String id_usuario);
    
    List<SessaoDeEstudo> findByGrupoDeEstudo_IdAndDisciplina(UUID id_grupo, String disciplina);

    List<SessaoDeEstudo> findByGrupoDeEstudo_IdAndTopico(UUID id_grupo, String topico);

}
