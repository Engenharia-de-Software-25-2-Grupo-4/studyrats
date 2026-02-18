package com.example.studyrats.dto.ConviteGrupo;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConviteResponseDTO {
    private UUID idGrupo;
    private String nomeGrupo;
    private String descricaoGrupo;
    private boolean jaMembro; // Importante para o Front tratar o Fluxo Secundário 2
}