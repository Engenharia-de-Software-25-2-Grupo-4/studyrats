package com.example.studyrats.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPostPutRequestDTO {

    @JsonProperty("name")
    @NotBlank(message = "Nome obrigatorio")
    private String name;

    @JsonProperty("email")
    @NotBlank(message = "Email obrigatorio")
    private String email;
}
