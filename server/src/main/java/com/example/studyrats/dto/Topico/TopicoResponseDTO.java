package com.example.studyrats.dto.Topico;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicoResponseDTO { 

    @JsonProperty("id_disciplina")
    private UUID id_comentario;

    @JsonProperty("nome")
    private String nome;
    
}
