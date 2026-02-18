package com.example.studyrats.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioSessao {
    
    @JsonProperty("id_comentario")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id_comentario;

    @ManyToOne
    @JoinColumn(name = "id_sessao", nullable = false)
    @JsonProperty("sessao_de_estudo")
    private SessaoDeEstudo sessaoDeEstudo;
    
    @ManyToOne
    @JoinColumn(name = "firebaseUid", nullable = false)
    @JsonProperty("autor")
    private Estudante autor;

    @Column(nullable = false, length = 500)
    @JsonProperty("texto")
    private String texto;

    @Column(nullable = false)
    @JsonProperty("horario_comentario")
    private LocalDateTime horarioComentario; 
    
}
