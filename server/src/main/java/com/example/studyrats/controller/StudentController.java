package com.example.studyrats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studyrats.dto.student.StudentPostPutRequestDTO;
import com.example.studyrats.exceptions.AccessDeniedException;
import com.example.studyrats.service.student.StudentService;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping("/students")
    public ResponseEntity<?> postMethodName(HttpServletRequest request, @RequestBody StudentPostPutRequestDTO studentPostPutRequestDTO) {
        String firebaseUid = getAuthenticatedUserId(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(studentService.criar(firebaseUid, studentPostPutRequestDTO));
    }
    
    private String getAuthenticatedUserId(HttpServletRequest request) {
        Object firebaseUser = request.getAttribute("firebaseUser");
        if (firebaseUser == null) {
            throw new AccessDeniedException();
        }
        return ((FirebaseToken) firebaseUser).getUid();
    }
    
}
