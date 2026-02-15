package com.example.studyrats.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDeEstudo {

    @JsonProperty("id_grupo")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("descricao")
    @Column(length = 1000)
    private String descricao;

    @JsonProperty("admin")
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Estudante admin;

    @Builder.Default
    @OneToMany(
        mappedBy = "grupo",
        cascade = jakarta.persistence.CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonIgnore
    private List<MembroGrupo> membros = new ArrayList<>();
}
