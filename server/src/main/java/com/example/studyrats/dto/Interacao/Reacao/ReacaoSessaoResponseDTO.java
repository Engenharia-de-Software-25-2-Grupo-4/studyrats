package com.example.studyrats.dto.Interacao.Reacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReacaoSessaoResponseDTO {

    @JsonProperty("reagiu")
    private boolean reagiu;
    
    @JsonProperty("total_reacoes")
    private Long totalReacoes;
    
}
