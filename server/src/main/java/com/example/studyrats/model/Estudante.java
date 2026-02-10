package com.example.studyrats.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Estudante {
    
    @Id
    private String firebaseUid;

    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "criador")
    private List<SessaoDeEstudo> studySessions = new ArrayList<>();

}
