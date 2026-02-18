package org.example.cenima.repository;

import org.example.cenima.entity.ProjectionFilm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ProjectionFilmRepository extends JpaRepository<ProjectionFilm, Long> {
    @Query(value = """
            SELECT p
            FROM ProjectionFilm p
            JOIN FETCH p.film f
            JOIN FETCH p.salle s
            JOIN FETCH s.cinema c
            JOIN FETCH c.ville v
            WHERE (:villeId IS NULL OR v.id = :villeId)
              AND (:cinemaId IS NULL OR c.id = :cinemaId)
              AND (:dateFrom IS NULL OR p.dateProjection >= :dateFrom)
              AND (:dateTo IS NULL OR p.dateProjection < :dateTo)
            """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM ProjectionFilm p
                    JOIN p.salle s
                    JOIN s.cinema c
                    JOIN c.ville v
                    WHERE (:villeId IS NULL OR v.id = :villeId)
                      AND (:cinemaId IS NULL OR c.id = :cinemaId)
                      AND (:dateFrom IS NULL OR p.dateProjection >= :dateFrom)
                      AND (:dateTo IS NULL OR p.dateProjection < :dateTo)
                    """)
    Page<ProjectionFilm> searchByFilters(@Param("villeId") Long villeId,
                                         @Param("cinemaId") Long cinemaId,
                                         @Param("dateFrom") Date dateFrom,
                                         @Param("dateTo") Date dateTo,
                                         Pageable pageable);
}
