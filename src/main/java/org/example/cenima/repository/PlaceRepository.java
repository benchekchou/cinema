package org.example.cenima.repository;

import org.example.cenima.entity.Place;
import org.example.cenima.repository.projection.AvailablePlaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query(value = """
            SELECT
                p.id AS placeId,
                p.numero AS numero,
                s.id AS salleId,
                s.name AS salleName
            FROM Place p
            JOIN p.salle s
            JOIN s.projectionFilms pf
            WHERE pf.id = :projectionId
              AND NOT EXISTS (
                    SELECT 1
                    FROM Ticket t
                    WHERE t.place = p
                      AND t.projectionFilm = pf
                      AND t.reservee = true
              )
            """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM Place p
                    JOIN p.salle s
                    JOIN s.projectionFilms pf
                    WHERE pf.id = :projectionId
                      AND NOT EXISTS (
                            SELECT 1
                            FROM Ticket t
                            WHERE t.place = p
                              AND t.projectionFilm = pf
                              AND t.reservee = true
                      )
                    """)
    Page<AvailablePlaceProjection> findAvailablePlacesByProjectionId(@Param("projectionId") Long projectionId, Pageable pageable);
}
