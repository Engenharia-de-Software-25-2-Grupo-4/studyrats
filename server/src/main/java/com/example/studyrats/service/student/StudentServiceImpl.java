package com.example.studyrats.service.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;
import com.example.studyrats.model.Student;
import com.example.studyrats.repository.StudentRepository;

import org.modelmapper.ModelMapper;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public StudentResponseDTO criar(StudentPostPutRequestDTO studentPostPutRequestDTO) {
        Student student = modelMapper.map(studentPostPutRequestDTO, Student.class);
        studentRepository.save(student);
        return modelMapper.map(student, StudentResponseDTO.class);
    }
}
