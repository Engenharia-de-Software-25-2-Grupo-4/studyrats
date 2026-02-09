package com.example.studyrats.dto.estudante;

import com.example.studyrats.model.Estudante;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudanteResponseDTO {

    private String firebaseUid;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("email")
    private String email;

    public EstudanteResponseDTO(Estudante estudante) {
        this.firebaseUid = estudante.getId();
        this.nome = estudante.getNome();
        this.email = estudante.getEmail();
    }
}
