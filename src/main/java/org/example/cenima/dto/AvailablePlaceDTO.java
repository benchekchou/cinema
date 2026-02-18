package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AvailablePlace", description = "Place disponible pour une projection")
public class AvailablePlaceDTO {
    @Schema(description = "Identifiant de la place", example = "12")
    private Long placeId;
    @Schema(description = "Numero de place", example = "8")
    private Integer numero;
    @Schema(description = "Identifiant de la salle", example = "23")
    private Long salleId;
    @Schema(description = "Nom de la salle", example = "Salle1")
    private String salleName;
}
