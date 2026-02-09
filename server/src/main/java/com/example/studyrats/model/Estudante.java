package com.example.studyrats.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Estudante {
    
    @Id
    private String firebaseUid;

    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @OneToMany(mappedBy = "creator")
    private List<StudySession> studySessions = new ArrayList<>();

    public String getId() {
        return firebaseUid;
    }

    public void setId(String novaFirebaseUid) {
        this.firebaseUid = novaFirebaseUid;
    }
    public String getNome() {
        return nome;
    }
    
    public void setName(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<StudySession> getStudySessions() {
        return studySessions;
    }
    public void setStudySessions(List<StudySession> studySessions) {
        this.studySessions = studySessions;
    }

}
