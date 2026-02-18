package com.example.studyrats.dto.Interacao.Comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioSessaoResponseDTO { 

    @JsonProperty("id_comentario")
    private UUID id_comentario;

    @JsonProperty("firebaseUid_autor")
    private String firebaseUid_autor;

    @JsonProperty("nome_autor")
    private String nome_autor;

    @JsonProperty("texto")
    private String texto;

    @JsonProperty("horario_comentario")
    private LocalDateTime horarioComentario;
    
}
