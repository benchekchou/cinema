package org.example.cenima.repository;

import org.example.cenima.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilmRepository extends JpaRepository<Film, Long> {
    @Query("SELECT f FROM Film f JOIN FETCH f.categorie")
    List<Film> findAllWithCategorie();

    @Query(value = """
            SELECT f
            FROM Film f
            JOIN FETCH f.categorie c
            WHERE c.id = :categorieId
            """,
            countQuery = """
                    SELECT COUNT(f)
                    FROM Film f
                    WHERE f.categorie.id = :categorieId
                    """)
    Page<Film> searchByCategorieId(@Param("categorieId") Long categorieId, Pageable pageable);
}
