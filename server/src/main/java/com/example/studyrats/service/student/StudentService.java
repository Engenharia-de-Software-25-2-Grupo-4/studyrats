package com.example.studyrats.service.student;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;

public interface StudentService {

    StudentResponseDTO criar(StudentPostPutRequestDTO studentPostPutRequestDTO);
    
}
