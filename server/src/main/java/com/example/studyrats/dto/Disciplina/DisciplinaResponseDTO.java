package com.example.studyrats.dto.Disciplina;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaResponseDTO { 

    @JsonProperty("id_disciplina")
    private UUID id_comentario;

    @JsonProperty("nome")
    private String nome;
    
}