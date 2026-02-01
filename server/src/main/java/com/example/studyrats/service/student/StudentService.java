package com.example.studyrats.service.student;

import java.util.UUID;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;

public interface StudentService {

    StudentResponseDTO criar(StudentPostPutRequestDTO studentPostPutRequestDTO);

    UUID getAuthenticatedStudentId();
    
}
