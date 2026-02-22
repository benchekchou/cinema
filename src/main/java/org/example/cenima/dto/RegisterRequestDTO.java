package org.example.cenima.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "RegisterRequest", description = "Demande de creation de compte")
public class RegisterRequestDTO {
    @NotBlank
    @Email
    @Size(max = 255)
    @Schema(description = "Email utilisateur", example = "user@cinema.local")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "must contain at least one uppercase, one lowercase, one digit and one special character (@$!%*?&)"
    )
    @Schema(description = "Mot de passe (min 8 chars, 1 majuscule, 1 minuscule, 1 chiffre, 1 special)", example = "MyPass123!")
    private String password;
}
