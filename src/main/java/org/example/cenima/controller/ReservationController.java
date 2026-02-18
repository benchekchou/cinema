package org.example.cenima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.ReservationService;
import org.example.cenima.dto.ReservationRequestDTO;
import org.example.cenima.dto.ReservationResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Operations de reservation de places")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    @Operation(
            summary = "Reserver une place",
            description = "Cree une reservation pour une place et une projection donnees. Retourne le ticket reserve avec un code de paiement unique."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Informations de reservation",
            content = @Content(
                    schema = @Schema(implementation = ReservationRequestDTO.class),
                    examples = @ExampleObject(
                            name = "ReservationExample",
                            value = "{\"nomClient\":\"Hamza Benchekchou\",\"placeId\":12,\"projectionId\":45}"
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation creee",
                    content = @Content(
                            schema = @Schema(implementation = ReservationResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "ReservationCreated",
                                    value = "{\"ticketId\":8801,\"placeId\":12,\"projectionId\":45,\"nomClient\":\"Hamza Benchekchou\",\"prix\":70.0,\"codePayement\":100245,\"reservee\":true}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Requete invalide"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces refuse"),
            @ApiResponse(responseCode = "409", description = "Place deja reservee")
    })
    public ResponseEntity<ReservationResponseDTO> reserve(@Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {
        ReservationResponseDTO reservation = reservationService.reserve(reservationRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/tickets/{id}")
                .buildAndExpand(reservation.getTicketId())
                .toUri();
        return ResponseEntity.created(location).body(reservation);
    }
}
