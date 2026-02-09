package com.example.studyrats.dto.estudante;

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
public class EstudantePostPutRequestDTO {

    @JsonProperty("name")
    @NotBlank(message = "Nome obrigatório")
    private String name;

    @JsonProperty("email")
    @NotBlank(message = "Email obrigatório")
    private String email;

}