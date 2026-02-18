package org.example.cenima.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI cinemaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema API")
                        .description("API de gestion cinema : authentification, films, reservations et recherches de projections.")
                        .version("v1")
                        .contact(new Contact().name("Cinema API Team")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer token. Exemple: Bearer eyJhbGciOi...")));
    }
}
