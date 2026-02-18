package com.example.studyrats.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class SessaoDeEstudo { 

    @JsonProperty("id_sessao")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_sessao;

    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    @JsonProperty("grupo_de_estudo")
    private GrupoDeEstudo grupoDeEstudo;
    
    @ManyToOne
    @JoinColumn(name = "firebaseUid", nullable = false)
    @JsonProperty("criador")
    private Estudante criador;
    
    // adicionei aqui, lembrar de ajustar os outros arquivos
    // @JsonProperty("id_grupo")
    // private UUID idGrupo;

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
