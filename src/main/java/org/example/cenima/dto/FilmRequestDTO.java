package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FilmRequest", description = "Payload de creation d'un film")
public class FilmRequestDTO {
    @NotBlank
    @Schema(description = "Titre du film", example = "Interstellar")
    private String titre;

    @Schema(description = "Description courte du film", example = "Un voyage spatial pour sauver l'humanite.")
    private String description;

    @NotNull
    @Positive
    @Schema(description = "Duree en heures", example = "2.49")
    private Double duree;

    @Schema(description = "Nom du realisateur", example = "Christopher Nolan")
    private String realisateur;

    @Schema(description = "Nom du fichier image/affiche", example = "interstellar.jpg")
    private String photo;

    @Schema(description = "Date de sortie", example = "2014-11-07T00:00:00.000+00:00")
    private Date dateSortie;

    @NotNull
    @Schema(description = "Identifiant de la categorie", example = "3")
    private Long categorieId;
}
