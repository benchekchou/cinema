package org.example.cenima.mapper;

import org.example.cenima.entity.Place;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.entity.Ticket;
import org.example.cenima.dto.TicketDTO;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    public TicketDTO toDto(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .nomClient(ticket.getNomClient())
                .prix(ticket.getPrix())
                .codePayement(ticket.getCodePayement())
                .reservee(ticket.isReservee())
                .placeId(ticket.getPlace() != null ? ticket.getPlace().getId() : null)
                .projectionId(ticket.getProjectionFilm() != null ? ticket.getProjectionFilm().getId() : null)
                .build();
    }

    public Ticket toEntity(TicketDTO ticketDTO, Place place, ProjectionFilm projectionFilm) {
        Ticket ticket = new Ticket();
        updateEntity(ticket, ticketDTO, place, projectionFilm);
        return ticket;
    }

    public void updateEntity(Ticket ticket, TicketDTO ticketDTO, Place place, ProjectionFilm projectionFilm) {
        ticket.setNomClient(ticketDTO.getNomClient());
        ticket.setPrix(ticketDTO.getPrix());
        ticket.setCodePayement(ticketDTO.getCodePayement());
        ticket.setReservee(ticketDTO.getReservee());
        ticket.setPlace(place);
        ticket.setProjectionFilm(projectionFilm);
    }
}
