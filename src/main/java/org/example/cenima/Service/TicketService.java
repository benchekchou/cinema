package org.example.cenima.Service;

import org.example.cenima.dto.TicketDTO;

import java.util.List;

public interface TicketService {
    List<TicketDTO> findAll();

    TicketDTO findById(Long id);

    TicketDTO create(TicketDTO ticketDTO);

    TicketDTO update(Long id, TicketDTO ticketDTO);

    void delete(Long id);
}
