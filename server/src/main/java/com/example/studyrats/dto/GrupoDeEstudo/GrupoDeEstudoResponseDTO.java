package com.example.studyrats.dto.GrupoDeEstudo;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.studyrats.model.Estudante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDeEstudoResponseDTO {

    @JsonProperty("id_grupo")
    private UUID id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("admin")
    private Estudante admin;

    @JsonProperty("foto_perfil")
    private String fotoPerfil;

    @JsonProperty("regras")
    private String regras;

    @JsonProperty("data_inicio")
    private LocalDateTime dataInicio;

    @JsonProperty("data_fim")
    private LocalDateTime dataFim;

    @Override
    public boolean equals(Object  o) {
        if (o == null || getClass() != o.getClass()) return false;

        GrupoDeEstudoResponseDTO outroGrupo = (GrupoDeEstudoResponseDTO) o;

        boolean idIgual = this.id.equals(outroGrupo.getId());
        boolean nomeIgual = this.nome.equals(outroGrupo.getNome());
        boolean descricaoIgual = this.descricao.equals(outroGrupo.getDescricao());
        boolean idAdminIgual = this.admin.getFirebaseUid().equals(outroGrupo.getAdmin().getFirebaseUid());

        return idIgual && nomeIgual && descricaoIgual && idAdminIgual;
    }
}
