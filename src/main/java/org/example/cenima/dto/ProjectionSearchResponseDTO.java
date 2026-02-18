package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ProjectionSearchResponse", description = "Projection retournee par la recherche")
public class ProjectionSearchResponseDTO {
    @Schema(description = "Identifiant projection", example = "45")
    private Long projectionId;
    @Schema(description = "Date/heure de projection", example = "2026-03-10T19:00:00.000+00:00")
    private Date dateProjection;
    @Schema(description = "Prix du ticket", example = "70.0")
    private Double prix;
    @Schema(description = "Identifiant film", example = "101")
    private Long filmId;
    @Schema(description = "Titre du film", example = "Interstellar")
    private String filmTitre;
    @Schema(description = "Identifiant ville", example = "1")
    private Long villeId;
    @Schema(description = "Nom de la ville", example = "Casablanca")
    private String villeNom;
    @Schema(description = "Identifiant cinema", example = "9")
    private Long cinemaId;
    @Schema(description = "Nom du cinema", example = "MegaRama")
    private String cinemaNom;
    @Schema(description = "Identifiant salle", example = "23")
    private Long salleId;
    @Schema(description = "Nom de salle", example = "Salle1")
    private String salleNom;
}
