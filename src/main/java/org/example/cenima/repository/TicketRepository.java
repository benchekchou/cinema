package org.example.cenima.repository;

import jakarta.persistence.LockModeType;
import org.example.cenima.entity.Ticket;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByPlace_IdAndProjectionFilm_Id(Long placeId, Long projectionId);

    boolean existsByPlace_IdAndProjectionFilm_IdAndIdNot(Long placeId, Long projectionId, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.place.id = :placeId AND t.projectionFilm.id = :projectionId")
    Optional<Ticket> findByPlaceIdAndProjectionIdForUpdate(@Param("placeId") Long placeId, @Param("projectionId") Long projectionId);

    @Query(value = "SELECT nextval('ticket_payment_code_seq')", nativeQuery = true)
    Long nextPaymentCode();
}
