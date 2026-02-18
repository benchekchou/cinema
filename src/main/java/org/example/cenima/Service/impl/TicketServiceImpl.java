package org.example.cenima.Service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cenima.entity.Place;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.example.cenima.entity.Ticket;
import org.example.cenima.dto.TicketDTO;
import org.example.cenima.exception.ConflictException;
import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.mapper.TicketMapper;
import org.example.cenima.repository.TicketRepository;
import org.example.cenima.Service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final PlaceRepository placeRepository;
    private final ProjectionFilmRepository projectionFilmRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> findAll() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO findById(Long id) {
        return ticketMapper.toDto(findTicketOrThrow(id));
    }

    @Override
    public TicketDTO create(TicketDTO ticketDTO) {
        Place place = findPlaceOrThrow(ticketDTO.getPlaceId());
        ProjectionFilm projectionFilm = findProjectionOrThrow(ticketDTO.getProjectionId());
        ensureSeatNotAlreadyAssigned(ticketDTO.getPlaceId(), ticketDTO.getProjectionId());

        Ticket ticket = ticketMapper.toEntity(ticketDTO, place, projectionFilm);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public TicketDTO update(Long id, TicketDTO ticketDTO) {
        Ticket existingTicket = findTicketOrThrow(id);
        Place place = findPlaceOrThrow(ticketDTO.getPlaceId());
        ProjectionFilm projectionFilm = findProjectionOrThrow(ticketDTO.getProjectionId());
        ensureSeatNotAlreadyAssignedForOtherTicket(id, ticketDTO.getPlaceId(), ticketDTO.getProjectionId());

        ticketMapper.updateEntity(existingTicket, ticketDTO, place, projectionFilm);
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Override
    public void delete(Long id) {
        Ticket existingTicket = findTicketOrThrow(id);
        ticketRepository.delete(existingTicket);
    }

    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id " + id));
    }

    private Place findPlaceOrThrow(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Place not found with id " + id));
    }

    private ProjectionFilm findProjectionOrThrow(Long id) {
        return projectionFilmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projection not found with id " + id));
    }

    private void ensureSeatNotAlreadyAssigned(Long placeId, Long projectionId) {
        if (ticketRepository.existsByPlace_IdAndProjectionFilm_Id(placeId, projectionId)) {
            throw new ConflictException("A ticket already exists for place " + placeId + " and projection " + projectionId);
        }
    }

    private void ensureSeatNotAlreadyAssignedForOtherTicket(Long ticketId, Long placeId, Long projectionId) {
        if (ticketRepository.existsByPlace_IdAndProjectionFilm_IdAndIdNot(placeId, projectionId, ticketId)) {
            throw new ConflictException("A ticket already exists for place " + placeId + " and projection " + projectionId);
        }
    }
}
