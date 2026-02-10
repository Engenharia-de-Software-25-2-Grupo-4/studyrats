package com.example.studyrats.dto.SessaoDeEstudo;

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

public class SessaoDeEstudoResponseDTO {
    @JsonProperty("id_sessao")
    private UUID idSessao;

    @JsonProperty("id_criador")
    private String idCriador; 

    @JsonProperty("nome_criador")
    private String nomeCriador;

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("horario_inicio")
    private LocalDateTime horarioInicio;

    @JsonProperty("duracao_minutos")
    private Integer duracaoMinutos;

    @JsonProperty("url_foto")
    private String urlFoto;

    @JsonProperty("disciplina")
    private String disciplina;

    @JsonProperty("topico")
    private String topico; 
}
