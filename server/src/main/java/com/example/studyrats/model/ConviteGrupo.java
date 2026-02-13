package com.example.studyrats.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoDeEstudo grupo;

    @ManyToOne
    @JoinColumn(name = "convidante_id", nullable = false)
    private Estudante convidante;

    @JsonProperty("uid_convidado")
    private String uidConvidado;

    @JsonProperty("status")
    private String status;
}
