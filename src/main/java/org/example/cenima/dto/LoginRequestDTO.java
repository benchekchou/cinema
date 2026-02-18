package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginRequest", description = "Demande de connexion")
public class LoginRequestDTO {
    @NotBlank
    @Email
    @Schema(description = "Email utilisateur", example = "client@cinema.local")
    private String email;

    @NotBlank
    @Schema(description = "Mot de passe utilisateur", example = "Client123!")
    private String password;
}
