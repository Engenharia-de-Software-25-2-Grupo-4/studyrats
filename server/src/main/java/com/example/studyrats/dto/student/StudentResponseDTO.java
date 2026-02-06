package com.example.studyrats.dto.student;

import java.util.UUID;

import com.example.studyrats.model.Student;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    public StudentResponseDTO(Student student) {
        this.id = student.getId();
        this.name = student.getName();
        this.email = student.getEmail();
    }
}
