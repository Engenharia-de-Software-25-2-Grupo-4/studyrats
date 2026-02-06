package com.example.studyrats.service.student;

import java.util.List;
import java.util.UUID;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;

public interface StudentService {

    StudentResponseDTO criar(StudentPostPutRequestDTO studentPostPutRequestDTO);

    List<StudentResponseDTO> listarTodos();
    
    StudentResponseDTO buscarPorId(UUID id);
    
    StudentResponseDTO atualizar(UUID id, StudentPostPutRequestDTO dto);
    
    void excluir(UUID id);
    
    UUID getAuthenticatedStudentId();
    
}
