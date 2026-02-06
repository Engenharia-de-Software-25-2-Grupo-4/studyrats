package com.example.studyrats.service.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;
import com.example.studyrats.exceptions.EmailAlreadyRegisteredException;
import com.example.studyrats.exceptions.PasswordMismatchException;
import com.example.studyrats.exceptions.StudentNotFoundException;
import com.example.studyrats.model.Student;
import com.example.studyrats.repository.StudentRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentRepository studentRepository;
    @Autowired
    ModelMapper modelMapper;

    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public StudentResponseDTO criar(StudentPostPutRequestDTO dto) {
    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
        throw new PasswordMismatchException();
    }

    if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
        throw new EmailAlreadyRegisteredException();
    }

    Student student = modelMapper.map(dto, Student.class);
    //student.setPassword(passwordEncoder.encode(dto.getPassword()));

    studentRepository.save(student);

    return modelMapper.map(student, StudentResponseDTO.class);
    }

    @Override
    public List<StudentResponseDTO> listarTodos() {
        return studentRepository.findAll().stream()
                .map(s -> modelMapper.map(s, StudentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO buscarPorId(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id.toString()));
        return modelMapper.map(student, StudentResponseDTO.class);
    }

    @Override
    public StudentResponseDTO atualizar(UUID id, StudentPostPutRequestDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado"));
        
        student.setName(dto.getName());

        student.setEmail(dto.getEmail()); 

        studentRepository.save(student);
        
        return modelMapper.map(student, StudentResponseDTO.class);
    }

    @Override
    public void excluir(UUID id) {
        studentRepository.deleteById(id);
    }

    @Override
    public UUID getAuthenticatedStudentId() {
        // falta implementar autenticação
        throw new UnsupportedOperationException("Unimplemented method 'getAuthenticatedStudentId'");
    }
}