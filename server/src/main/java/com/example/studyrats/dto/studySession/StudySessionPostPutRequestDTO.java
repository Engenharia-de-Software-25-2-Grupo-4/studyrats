package com.example.studyrats.dto.studySession;

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
public class StudySessionPostPutRequestDTO {
    
    @JsonProperty("title")
    @NotBlank(message = "Título obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String title;

    @JsonProperty("description")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String description;

    @JsonProperty("start_time")
    @NotNull(message = "Horário de início obrigatório")
    private LocalDateTime startTime;

    @JsonProperty("duration_minutes")
    @NotNull(message = "Duração obrigatória")
    @Min(value = 1, message = "A duração deve ser de no mínimo 1 minuto")
    private Integer durationMinutes;

    @JsonProperty("url_photo")
    private String urlPhoto; 

    @JsonProperty("subject")
    @NotBlank(message = "Disciplina obrigatória")
    @Size(max = 100, message = "A disciplina deve ter no máximo 100 caracteres")
    private String subject;

    @JsonProperty("topic") 
    @NotBlank(message = "Tópico obrigatório")
    @Size(max = 100, message = "O tópico deve ter no máximo 100 caracteres")
    private String topic;
}
