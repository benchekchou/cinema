package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ReservationRequest", description = "Demande de reservation d'une place pour une projection")
public class ReservationRequestDTO {
    @NotBlank
    @Size(max = 255)
    @Schema(description = "Nom complet du client", example = "Hamza Benchekchou")
    private String nomClient;

    @NotNull
    @Positive
    @Schema(description = "Identifiant de la place a reserver", example = "12")
    private Long placeId;

    @NotNull
    @Positive
    @Schema(description = "Identifiant de la projection ciblee", example = "45")
    private Long projectionId;
}
