package com.example.studyrats.dto.estudante;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de requisição para criação ou atualização de um estudante")
public class EstudantePostPutRequestDTO {

    @Schema(description = "Nome completo do estudante", example = "Ana Maria")
    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatório")
    private String nome;

    @Schema(description = "E-mail de contato do estudante", example = "ana.maria@exemplo.com")
    @JsonProperty("email")
    @NotBlank(message = "Email obrigatório")
    private String email;

}