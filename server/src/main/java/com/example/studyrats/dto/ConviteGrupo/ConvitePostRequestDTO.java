package com.example.studyrats.dto.ConviteGrupo;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvitePostRequestDTO {

    @JsonProperty("id_grupo")
    @NotNull(message = "O ID do grupo é obrigatório")
    private UUID idGrupo;

    @JsonProperty("uid_convidado")
    @NotBlank(message = "O UID do convidado é obrigatório")
    private String uidConvidado;
}
