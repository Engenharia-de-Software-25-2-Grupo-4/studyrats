package com.example.studyrats.service.Disciplina;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.Disciplina.DisciplinaResponseDTO;

public interface DisciplinaService {

    List<DisciplinaResponseDTO> listarDisciplinas();

    List<DisciplinaResponseDTO> listarDisciplinasPorUsuario(String idUsuario);
    
    List<DisciplinaResponseDTO> listarDisciplinasPorGrupo(UUID idGrupo, String idUsuario);

    List<DisciplinaResponseDTO> listarDisciplinasPorUsuarioEmGrupo(String idUsuario, UUID idGrupo);
    
}
