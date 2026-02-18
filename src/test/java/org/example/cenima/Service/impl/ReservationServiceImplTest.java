package org.example.cenima.Service.impl;

import org.example.cenima.dto.ReservationRequestDTO;
import org.example.cenima.dto.ReservationResponseDTO;
import org.example.cenima.entity.Place;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.entity.Salle;
import org.example.cenima.entity.Ticket;
import org.example.cenima.exception.ConflictException;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.example.cenima.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private ProjectionFilmRepository projectionFilmRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void reserve_shouldReserveTicket_whenTicketExistsAndNotReserved() {
        Place place = place(12L, 4L);
        ProjectionFilm projectionFilm = projection(45L, 4L, 70.0);
        Ticket existingTicket = ticket(8801L, place, projectionFilm, false);

        when(placeRepository.findById(12L)).thenReturn(Optional.of(place));
        when(projectionFilmRepository.findById(45L)).thenReturn(Optional.of(projectionFilm));
        when(ticketRepository.findByPlaceIdAndProjectionIdForUpdate(12L, 45L)).thenReturn(Optional.of(existingTicket));
        when(ticketRepository.nextPaymentCode()).thenReturn(100245L);
        when(ticketRepository.save(existingTicket)).thenReturn(existingTicket);

        ReservationResponseDTO response = reservationService.reserve(request("  Hamza Benchekchou  ", 12L, 45L));

        assertThat(existingTicket.isReservee()).isTrue();
        assertThat(existingTicket.getNomClient()).isEqualTo("Hamza Benchekchou");
        assertThat(existingTicket.getCodePayement()).isEqualTo(100245);
        assertThat(response.getTicketId()).isEqualTo(8801L);
        assertThat(response.getReservee()).isTrue();
        verify(ticketRepository).save(existingTicket);
    }

    @Test
    void reserve_shouldCreateTicketAndReserveIt_whenNoTicketExists() {
        Place place = place(12L, 4L);
        ProjectionFilm projectionFilm = projection(45L, 4L, 70.0);

        when(placeRepository.findById(12L)).thenReturn(Optional.of(place));
        when(projectionFilmRepository.findById(45L)).thenReturn(Optional.of(projectionFilm));
        when(ticketRepository.findByPlaceIdAndProjectionIdForUpdate(12L, 45L)).thenReturn(Optional.empty());
        when(ticketRepository.saveAndFlush(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket created = invocation.getArgument(0);
            created.setId(991L);
            return created;
        });
        when(ticketRepository.nextPaymentCode()).thenReturn(100300L);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponseDTO response = reservationService.reserve(request("Client One", 12L, 45L));

        assertThat(response.getTicketId()).isEqualTo(991L);
        assertThat(response.getPlaceId()).isEqualTo(12L);
        assertThat(response.getProjectionId()).isEqualTo(45L);
        assertThat(response.getCodePayement()).isEqualTo(100300);
        assertThat(response.getReservee()).isTrue();
        verify(ticketRepository).saveAndFlush(any(Ticket.class));
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void reserve_shouldReject_whenTicketAlreadyReserved() {
        Place place = place(12L, 4L);
        ProjectionFilm projectionFilm = projection(45L, 4L, 70.0);
        Ticket existingTicket = ticket(8801L, place, projectionFilm, true);

        when(placeRepository.findById(12L)).thenReturn(Optional.of(place));
        when(projectionFilmRepository.findById(45L)).thenReturn(Optional.of(projectionFilm));
        when(ticketRepository.findByPlaceIdAndProjectionIdForUpdate(12L, 45L)).thenReturn(Optional.of(existingTicket));

        assertThatThrownBy(() -> reservationService.reserve(request("Client Two", 12L, 45L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Place deja reservee");

        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(ticketRepository, never()).nextPaymentCode();
    }

    @Test
    void reserve_shouldReject_whenPlaceDoesNotBelongToProjectionSalle() {
        Place place = place(12L, 1L);
        ProjectionFilm projectionFilm = projection(45L, 2L, 70.0);

        when(placeRepository.findById(12L)).thenReturn(Optional.of(place));
        when(projectionFilmRepository.findById(45L)).thenReturn(Optional.of(projectionFilm));

        assertThatThrownBy(() -> reservationService.reserve(request("Client", 12L, 45L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Place invalide pour cette projection");

        verifyNoInteractions(ticketRepository);
    }

    private ReservationRequestDTO request(String nomClient, Long placeId, Long projectionId) {
        return ReservationRequestDTO.builder()
                .nomClient(nomClient)
                .placeId(placeId)
                .projectionId(projectionId)
                .build();
    }

    private Place place(Long id, Long salleId) {
        Salle salle = new Salle();
        salle.setId(salleId);

        Place place = new Place();
        place.setId(id);
        place.setSalle(salle);
        return place;
    }

    private ProjectionFilm projection(Long id, Long salleId, Double prix) {
        Salle salle = new Salle();
        salle.setId(salleId);

        ProjectionFilm projectionFilm = new ProjectionFilm();
        projectionFilm.setId(id);
        projectionFilm.setSalle(salle);
        projectionFilm.setPrix(prix);
        return projectionFilm;
    }

    private Ticket ticket(Long id, Place place, ProjectionFilm projectionFilm, boolean reservee) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setPlace(place);
        ticket.setProjectionFilm(projectionFilm);
        ticket.setReservee(reservee);
        ticket.setPrix(projectionFilm.getPrix());
        ticket.setCodePayement(0);
        return ticket;
    }
}
