package org.example.cenima.Service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.ReservationService;
import org.example.cenima.dto.ReservationRequestDTO;
import org.example.cenima.dto.ReservationResponseDTO;
import org.example.cenima.entity.Place;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.entity.Ticket;
import org.example.cenima.exception.ConflictException;
import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.example.cenima.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final ProjectionFilmRepository projectionFilmRepository;

    @Override
    @Transactional
    public ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO) {
        Place place = findPlaceOrThrow(reservationRequestDTO.getPlaceId());
        ProjectionFilm projectionFilm = findProjectionOrThrow(reservationRequestDTO.getProjectionId());
        validatePlaceBelongsToProjection(place, projectionFilm);

        Ticket ticket = ticketRepository.findByPlaceIdAndProjectionIdForUpdate(place.getId(), projectionFilm.getId())
                .orElseGet(() -> createTicketIfMissing(place, projectionFilm));

        if (ticket.isReservee()) {
            throw new ConflictException("Place deja reservee pour cette projection");
        }

        ticket.setNomClient(reservationRequestDTO.getNomClient().trim());
        ticket.setPrix(projectionFilm.getPrix());
        ticket.setCodePayement(nextUniquePaymentCode());
        ticket.setReservee(true);

        Ticket reservedTicket = ticketRepository.save(ticket);
        return toResponseDto(reservedTicket);
    }

    private Place findPlaceOrThrow(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id " + placeId));
    }

    private ProjectionFilm findProjectionOrThrow(Long projectionId) {
        return projectionFilmRepository.findById(projectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Projection not found with id " + projectionId));
    }

    private void validatePlaceBelongsToProjection(Place place, ProjectionFilm projectionFilm) {
        if (place.getSalle() == null || projectionFilm.getSalle() == null || place.getSalle().getId() == null
                || projectionFilm.getSalle().getId() == null
                || !place.getSalle().getId().equals(projectionFilm.getSalle().getId())) {
            throw new ConflictException("Place invalide pour cette projection");
        }
    }

    private Ticket createTicketIfMissing(Place place, ProjectionFilm projectionFilm) {
        Ticket ticket = new Ticket();
        ticket.setPlace(place);
        ticket.setProjectionFilm(projectionFilm);
        ticket.setPrix(projectionFilm.getPrix());
        ticket.setCodePayement(0);
        ticket.setReservee(false);

        try {
            return ticketRepository.saveAndFlush(ticket);
        } catch (DataIntegrityViolationException exception) {
            return ticketRepository.findByPlaceIdAndProjectionIdForUpdate(place.getId(), projectionFilm.getId())
                    .orElseThrow(() -> new ConflictException("Reservation concurrente detectee, veuillez reessayer"));
        }
    }

    private int nextUniquePaymentCode() {
        Long paymentCode = ticketRepository.nextPaymentCode();
        if (paymentCode == null || paymentCode > Integer.MAX_VALUE) {
            throw new IllegalStateException("Unable to generate payment code");
        }
        return paymentCode.intValue();
    }

    private ReservationResponseDTO toResponseDto(Ticket ticket) {
        return ReservationResponseDTO.builder()
                .ticketId(ticket.getId())
                .placeId(ticket.getPlace().getId())
                .projectionId(ticket.getProjectionFilm().getId())
                .nomClient(ticket.getNomClient())
                .prix(ticket.getPrix())
                .codePayement(ticket.getCodePayement())
                .reservee(ticket.isReservee())
                .build();
    }
}
