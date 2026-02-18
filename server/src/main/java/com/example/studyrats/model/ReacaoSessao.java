package com.example.studyrats.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
@Table (
    name = "reacao_sessao",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_sessao", "firebaseUid"})
    }
)

public class ReacaoSessao {
    @JsonProperty("id_reacao")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_reacao;

    @ManyToOne
    @JoinColumn(name = "id_sessao", nullable = false)
    @JsonProperty("sessao_de_estudo")
    private SessaoDeEstudo sessaoDeEstudo;
    
    @ManyToOne
    @JoinColumn(name = "firebaseUid", nullable = false)
    @JsonProperty("autor")
    private Estudante autor;

    @Column(nullable = false)   
    @JsonProperty("horario_reacao")
    private LocalDateTime horarioReacao; 
    
}
