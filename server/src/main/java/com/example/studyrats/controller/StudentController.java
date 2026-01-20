package com.example.studyrats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.service.student.StudentService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping("/students")
    public ResponseEntity<?> postMethodName(@RequestBody StudentPostPutRequestDTO studentPostPutRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(studentService.criar(studentPostPutRequestDTO));
    }
    
    
}
