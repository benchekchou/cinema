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
@Schema(name = "ReservationResponse", description = "Resultat de reservation d'une place")
public class ReservationResponseDTO {
    @Schema(description = "Identifiant du ticket reserve", example = "8801")
    private Long ticketId;
    @Schema(description = "Identifiant de la place reservee", example = "12")
    private Long placeId;
    @Schema(description = "Identifiant de la projection", example = "45")
    private Long projectionId;
    @Schema(description = "Nom du client", example = "Hamza Benchekchou")
    private String nomClient;
    @Schema(description = "Prix applique a la reservation", example = "70.0")
    private Double prix;
    @Schema(description = "Code unique de paiement", example = "100245")
    private Integer codePayement;
    @Schema(description = "Etat de reservation", example = "true")
    private Boolean reservee;
}
