package org.example.cenima.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;

    @Size(max = 255)
    private String nomClient;

    @NotNull
    @PositiveOrZero
    private Double prix;

    @NotNull
    @Positive
    private Integer codePayement;

    @NotNull
    private Boolean reservee;

    @NotNull
    @Positive
    private Long placeId;

    @NotNull
    @Positive
    private Long projectionId;
}
