package com.example.studyrats.dto.GrupoDeEstudo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembroGrupoResponseDTO {
    private String nomeEstudante;
    private String firebaseUid;
    private String role;
    private Integer quantidadeCheckins;
}