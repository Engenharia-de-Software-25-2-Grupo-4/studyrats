package com.example.studyrats.dto.GrupoDeEstudo;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.studyrats.model.Estudante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDeEstudoResponseDTO {

    @JsonProperty("id_grupo")
    private UUID id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("admin")
    private Estudante admin;
}
