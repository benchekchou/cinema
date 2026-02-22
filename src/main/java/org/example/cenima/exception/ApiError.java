package org.example.cenima.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiError", description = "Format d'erreur standard de l'API")
public class ApiError {
    @Schema(description = "Horodatage UTC", example = "2026-02-22T14:30:00Z")
    private Instant timestamp;

    @Schema(description = "Code HTTP", example = "400")
    private int status;

    @Schema(description = "Libelle HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Message principal", example = "Requete invalide")
    private String message;

    @Schema(description = "Chemin de la requete", example = "/api/reservations")
    private String path;

    @Schema(description = "Details par champ (validation)")
    private List<FieldError> details;

    @Getter
    @Builder
    @Schema(name = "FieldError", description = "Erreur de validation sur un champ")
    public static class FieldError {
        @Schema(description = "Nom du champ", example = "email")
        private String field;

        @Schema(description = "Valeur rejetee", example = "not-an-email")
        private Object rejectedValue;

        @Schema(description = "Message d'erreur", example = "must be a well-formed email address")
        private String message;
    }
}
