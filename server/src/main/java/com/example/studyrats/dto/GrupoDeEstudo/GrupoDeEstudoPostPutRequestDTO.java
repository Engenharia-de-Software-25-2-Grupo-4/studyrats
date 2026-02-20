package com.example.studyrats.dto.GrupoDeEstudo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @JsonProperty("foto_perfil")
    private String fotoPerfil;

    @JsonProperty("regras")
    @Size(max = 2000, message = "As regras não podem ultrapassar 2000 caracteres")
    private String regras;

    @JsonProperty("data_inicio")
    private LocalDateTime dataInicio;

    @JsonProperty("data_fim")
    private LocalDateTime dataFim;
}
