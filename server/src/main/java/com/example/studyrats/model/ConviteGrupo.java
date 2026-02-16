package com.example.studyrats.model;

import java.util.UUID;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConviteGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id_convite")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoDeEstudo grupo;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Estudante criador;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(nullable = false)
    private boolean ativo = true;
}
