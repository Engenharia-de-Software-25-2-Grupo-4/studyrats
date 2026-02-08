package com.example.studyrats.dto.SessaoDeEstudo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessaoDeEstudoPostPutRequestDTO {

    @JsonProperty("titulo")
    @NotBlank(message = "Título obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String titulo;

    @JsonProperty("descricao")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @JsonProperty("horario_inicio")
    @NotNull(message = "Horário de início obrigatório")
    private LocalDateTime horarioInicio;

    @JsonProperty("duracao_minutos")
    @NotNull(message = "Duração obrigatória")
    @Min(value = 1, message = "A duração deve ser de no mínimo 1 minuto")
    private Integer duracaoMinutos;

    @JsonProperty("url_foto")
    private String urlFoto; 

    @JsonProperty("disciplina")
    @NotBlank(message = "Disciplina obrigatória")
    @Size(max = 100, message = "A disciplina deve ter no máximo 100 caracteres")
    private String disciplina;

    @JsonProperty("topico") 
    @NotBlank(message = "Tópico obrigatório")
    @Size(max = 100, message = "O tópico deve ter no máximo 100 caracteres")
    private String topico;
   
}
