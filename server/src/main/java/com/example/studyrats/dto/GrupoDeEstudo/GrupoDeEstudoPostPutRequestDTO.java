package com.example.studyrats.dto.GrupoDeEstudo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDeEstudoPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do grupo obrigatório")
    @Size(max = 200)
    private String nome;

    @JsonProperty("descricao")
    @Size(max = 1000)
    private String descricao;
}
