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
@Schema(name = "LoginResponse", description = "Reponse de connexion JWT")
public class LoginResponseDTO {
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "Type de token", example = "Bearer")
    private String tokenType;
    @Schema(description = "Duree de validite en millisecondes", example = "3600000")
    private long expiresIn;
    @Schema(description = "Email authentifie", example = "client@cinema.local")
    private String email;
    @Schema(description = "Role courant", example = "ROLE_CLIENT")
    private String role;
}
