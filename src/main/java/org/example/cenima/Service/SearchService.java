package org.example.cenima.Service;

import org.example.cenima.dto.AvailablePlaceDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.example.cenima.dto.ProjectionSearchResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SearchService {
    Page<FilmResponseDTO> searchFilmsByCategorie(Long categorieId, Pageable pageable);

    Page<ProjectionSearchResponseDTO> searchProjections(Long villeId, Long cinemaId, LocalDate date, Pageable pageable);

    Page<AvailablePlaceDTO> searchAvailablePlaces(Long projectionId, Pageable pageable);
}
