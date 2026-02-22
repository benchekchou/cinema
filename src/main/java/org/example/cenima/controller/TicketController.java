package org.example.cenima.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.TicketService;
import org.example.cenima.dto.TicketDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Gestion CRUD des tickets (ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    @Operation(summary = "Lister les tickets", description = "Retourne la liste de tous les tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des tickets"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN")
    })
    public ResponseEntity<List<TicketDTO>> findAll() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Rechercher un ticket", description = "Retourne un ticket par son identifiant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket trouve"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "404", description = "Ticket introuvable")
    })
    public ResponseEntity<TicketDTO> findById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Creer un ticket", description = "Cree un nouveau ticket (ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket cree"),
            @ApiResponse(responseCode = "400", description = "Requete invalide"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "409", description = "Ticket existant pour cette place/projection")
    })
    public ResponseEntity<TicketDTO> create(@RequestBody @Valid TicketDTO ticketDTO) {
        TicketDTO createdTicket = ticketService.create(ticketDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTicket.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTicket);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un ticket", description = "Met a jour un ticket existant (ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket mis a jour"),
            @ApiResponse(responseCode = "400", description = "Requete invalide"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "404", description = "Ticket introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit de place/projection")
    })
    public ResponseEntity<TicketDTO> update(@PathVariable @Positive Long id, @RequestBody @Valid TicketDTO ticketDTO) {
        return ResponseEntity.ok(ticketService.update(id, ticketDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un ticket", description = "Supprime un ticket par identifiant (ADMIN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket supprime"),
            @ApiResponse(responseCode = "401", description = "Authentification requise"),
            @ApiResponse(responseCode = "403", description = "Acces reserve ADMIN"),
            @ApiResponse(responseCode = "404", description = "Ticket introuvable")
    })
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
