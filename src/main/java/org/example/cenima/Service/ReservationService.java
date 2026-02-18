package org.example.cenima.Service;

import org.example.cenima.dto.ReservationRequestDTO;
import org.example.cenima.dto.ReservationResponseDTO;

public interface ReservationService {
    ReservationResponseDTO reserve(ReservationRequestDTO reservationRequestDTO);
}
