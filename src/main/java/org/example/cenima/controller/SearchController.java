package org.example.cenima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.SearchService;
import org.example.cenima.dto.AvailablePlaceDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.example.cenima.dto.ProjectionSearchResponseDTO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Validated
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Projections", description = "Recherche de projections et disponibilites")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/films")
    @Operation(
            summary = "Rechercher les films par categorie",
            description = "Retourne une page de films filtres par categorie."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultat de recherche films",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "FilmsPageExample",
                                    value = "{\"content\":[{\"id\":101,\"titre\":\"Interstellar\",\"description\":\"Un voyage spatial pour sauver l'humanite.\",\"duree\":2.49,\"realisateur\":\"Christopher Nolan\",\"photo\":\"interstellar.jpg\",\"dateSortie\":\"2014-11-07T00:00:00.000+00:00\",\"categorieId\":3}],\"number\":0,\"size\":20,\"totalElements\":1,\"totalPages\":1}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parametres invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    public ResponseEntity<Page<FilmResponseDTO>> searchFilmsByCategorie(
            @Parameter(description = "Identifiant categorie", example = "3")
            @RequestParam("categorieId") @Positive Long categorieId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchFilmsByCategorie(categorieId, pageable));
    }

    @GetMapping("/projections")
    @Operation(
            summary = "Rechercher des projections",
            description = "Recherche paginee des projections par ville, cinema et/ou date (au moins un filtre requis)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultat de recherche projections",
                    content = @Content(
                            schema = @Schema(implementation = ProjectionSearchResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "ProjectionPageExample",
                                    value = "{\"content\":[{\"projectionId\":45,\"dateProjection\":\"2026-03-10T19:00:00.000+00:00\",\"prix\":70.0,\"filmId\":101,\"filmTitre\":\"Interstellar\",\"villeId\":1,\"villeNom\":\"Casablanca\",\"cinemaId\":9,\"cinemaNom\":\"MegaRama\",\"salleId\":23,\"salleNom\":\"Salle1\"}],\"number\":0,\"size\":20,\"totalElements\":1,\"totalPages\":1}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Filtres invalides"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces refuse")
    })
    public ResponseEntity<Page<ProjectionSearchResponseDTO>> searchProjections(
            @Parameter(description = "Filtre par ville", example = "1")
            @RequestParam(value = "villeId", required = false) @Positive Long villeId,
            @Parameter(description = "Filtre par cinema", example = "9")
            @RequestParam(value = "cinemaId", required = false) @Positive Long cinemaId,
            @Parameter(description = "Filtre par date (ISO-8601)", example = "2026-03-10")
            @RequestParam(value = "date", required = false) LocalDate date,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchProjections(villeId, cinemaId, date, pageable));
    }

    @GetMapping("/projections/{projectionId}/available-places")
    @Operation(
            summary = "Rechercher les places disponibles d'une projection",
            description = "Retourne les places non reservees pour une projection donnee."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultat des places disponibles",
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "AvailablePlacesPageExample",
                                    value = "{\"content\":[{\"placeId\":12,\"numero\":8,\"salleId\":23,\"salleName\":\"Salle1\"}],\"number\":0,\"size\":20,\"totalElements\":1,\"totalPages\":1}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces refuse"),
            @ApiResponse(responseCode = "404", description = "Projection introuvable")
    })
    public ResponseEntity<Page<AvailablePlaceDTO>> searchAvailablePlaces(
            @Parameter(description = "Identifiant projection", example = "45")
            @PathVariable @Positive Long projectionId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(searchService.searchAvailablePlaces(projectionId, pageable));
    }
}
