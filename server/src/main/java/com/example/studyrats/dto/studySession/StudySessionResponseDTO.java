package com.example.studyrats.dto.studySession;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionResponseDTO {
    
    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("creator_id")
    private UUID creatorId; 

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonProperty("duration_minutes")
    private Integer durationMinutes;

    @JsonProperty("url_photo")
    private String urlPhoto;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("topic")
    private String topic; 

}
