package com.example.studyrats.dto.Interacao.Comentario;

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
public class ComentarioSessaoPostPutRequestDTO {

    @JsonProperty("texto")
    @NotBlank(message = "Texto do comentário é obrigatório")
    @Size(max = 500, message = "Comentário deve ter no máximo 500 caracteres")
    private String texto;
    
}
