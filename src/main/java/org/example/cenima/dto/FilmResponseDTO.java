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
@Schema(name = "FilmResponse", description = "Representation d'un film")
public class FilmResponseDTO {
    @Schema(description = "Identifiant unique du film", example = "101")
    private Long id;
    @Schema(description = "Titre", example = "Interstellar")
    private String titre;
    @Schema(description = "Description", example = "Un voyage spatial pour sauver l'humanite.")
    private String description;
    @Schema(description = "Duree en heures", example = "2.49")
    private Double duree;
    @Schema(description = "Realisateur", example = "Christopher Nolan")
    private String realisateur;
    @Schema(description = "Nom du fichier image", example = "interstellar.jpg")
    private String photo;
    @Schema(description = "Date de sortie", example = "2014-11-07T00:00:00.000+00:00")
    private Date dateSortie;
    @Schema(description = "Identifiant categorie", example = "3")
    private Long categorieId;
}
