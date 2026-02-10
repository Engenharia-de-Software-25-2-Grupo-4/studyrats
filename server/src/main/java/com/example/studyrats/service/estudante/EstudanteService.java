package com.example.studyrats.service.estudante;

import java.util.List;

import com.example.studyrats.dto.estudante.EstudantePostPutRequestDTO;
import com.example.studyrats.dto.estudante.EstudanteResponseDTO;

public interface EstudanteService {

    EstudanteResponseDTO criar(EstudantePostPutRequestDTO studentPostPutRequestDTO, String uid);

    List<EstudanteResponseDTO> listarTodos();
    
    EstudanteResponseDTO buscarPorId(String firebaseUid);
    
    EstudanteResponseDTO atualizar(String firebaseUid, EstudantePostPutRequestDTO dto);
    
    void excluir(String firebaseUid);

}
