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
    @NotBlank(message = "Nome obrigatório")
    private String name;

    @JsonProperty("email")
    @NotBlank(message = "Email obrigatório")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Senha obrigatória")
    private String password; // Adicionado para RF-01 e RNF/SEG-04

    @NotBlank(message = "Confirmação de senha obrigatória")
    private String confirmPassword;

}