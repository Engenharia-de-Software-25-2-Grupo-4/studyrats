package com.example.studyrats.dto.Disciplina;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaPostPutRequestDTO {
    
    @JsonProperty("nome")
    @NotBlank(message = "Nome da disciplina é obrigatório")
    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    private String nome;

}
    