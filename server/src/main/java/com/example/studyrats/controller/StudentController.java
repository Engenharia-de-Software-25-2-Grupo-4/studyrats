package com.example.studyrats.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.dto.student.StudentResponseDTO;
import com.example.studyrats.service.student.StudentService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDTO> create(@RequestBody StudentPostPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAll() {
        return ResponseEntity.ok(studentService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> update(@PathVariable UUID id, @RequestBody StudentPostPutRequestDTO dto) {
        return ResponseEntity.ok(studentService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}