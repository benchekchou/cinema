package org.example.cenima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.FilmService;
import org.example.cenima.dto.FilmRequestDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/films")
@RequiredArgsConstructor
@Tag(name = "Films", description = "Gestion des films")
@SecurityRequirement(name = "bearerAuth")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    @Operation(summary = "Lister les films", description = "Retourne la liste des films disponibles.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des films",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FilmResponseDTO.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN")
    })
    public ResponseEntity<List<FilmResponseDTO>> findAll() {
        return ResponseEntity.ok(filmService.findAll());
    }

    @PostMapping
    @Operation(summary = "Creer un film", description = "Ajoute un film dans le catalogue cinema (ADMIN).")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Donnees du film a creer",
            content = @Content(
                    schema = @Schema(implementation = FilmRequestDTO.class),
                    examples = @ExampleObject(
                            name = "FilmCreateExample",
                            value = "{\"titre\":\"Interstellar\",\"description\":\"Un voyage spatial pour sauver l'humanite.\",\"duree\":2.49,\"realisateur\":\"Christopher Nolan\",\"photo\":\"interstellar.jpg\",\"dateSortie\":\"2014-11-07T00:00:00.000+00:00\",\"categorieId\":3}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Film cree",
                    content = @Content(schema = @Schema(implementation = FilmResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Requete invalide"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "404", description = "Categorie introuvable")
    })
    public ResponseEntity<FilmResponseDTO> create(@RequestBody @Valid FilmRequestDTO request) {
        FilmResponseDTO created = filmService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un film", description = "Supprime un film par identifiant (ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Film supprime"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "404", description = "Film introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        filmService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
