package com.example.studyrats.dto.estudante;

import com.example.studyrats.model.Estudante;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de resposta com os dados do perfil do estudante")
public class EstudanteResponseDTO {

    @Schema(description = "Identificador único gerado pelo Firebase", example = "uR7bW8xYz2Pq")
    private String firebaseUid;

    @Schema(description = "Nome do estudante cadastrado", example = "Ana Maria")
    @JsonProperty("nome")
    private String nome;

    @Schema(description = "E-mail do estudante cadastrado", example = "ana.maria@exemplo.com")
    @JsonProperty("email")
    private String email;

    public EstudanteResponseDTO(Estudante estudante) {
        this.firebaseUid = estudante.getFirebaseUid();
        this.nome = estudante.getNome();
        this.email = estudante.getEmail();
    }
}