package org.example.cenima.Service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.SearchService;
import org.example.cenima.dto.AvailablePlaceDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.example.cenima.dto.ProjectionSearchResponseDTO;
import org.example.cenima.entity.Film;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.example.cenima.repository.FilmRepository;
import org.example.cenima.repository.projection.AvailablePlaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchServiceImpl implements SearchService {
    private final FilmRepository filmRepository;
    private final ProjectionFilmRepository projectionFilmRepository;
    private final PlaceRepository placeRepository;

    @Override
    public Page<FilmResponseDTO> searchFilmsByCategorie(Long categorieId, Pageable pageable) {
        return filmRepository.searchByCategorieId(categorieId, pageable)
                .map(this::toFilmDto);
    }

    @Override
    public Page<ProjectionSearchResponseDTO> searchProjections(Long villeId, Long cinemaId, LocalDate date, Pageable pageable) {
        if (villeId == null && cinemaId == null && date == null) {
            throw new IllegalArgumentException("At least one filter is required: villeId, cinemaId or date");
        }

        Date dateFrom = null;
        Date dateTo = null;
        if (date != null) {
            dateFrom = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC));
            dateTo = Date.from(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        return projectionFilmRepository.searchByFilters(villeId, cinemaId, dateFrom, dateTo, pageable)
                .map(this::toProjectionDto);
    }

    @Override
    public Page<AvailablePlaceDTO> searchAvailablePlaces(Long projectionId, Pageable pageable) {
        if (!projectionFilmRepository.existsById(projectionId)) {
            throw new ResourceNotFoundException("Projection not found with id " + projectionId);
        }

        return placeRepository.findAvailablePlacesByProjectionId(projectionId, pageable)
                .map(this::toAvailablePlaceDto);
    }

    private FilmResponseDTO toFilmDto(Film film) {
        return FilmResponseDTO.builder()
                .id(film.getId())
                .titre(film.getTitre())
                .description(film.getDescription())
                .duree(film.getDuree())
                .realisateur(film.getRealisateur())
                .photo(film.getPhoto())
                .dateSortie(film.getDateSortie())
                .categorieId(film.getCategorie() != null ? film.getCategorie().getId() : null)
                .build();
    }

    private ProjectionSearchResponseDTO toProjectionDto(ProjectionFilm projectionFilm) {
        return ProjectionSearchResponseDTO.builder()
                .projectionId(projectionFilm.getId())
                .dateProjection(projectionFilm.getDateProjection())
                .prix(projectionFilm.getPrix())
                .filmId(projectionFilm.getFilm() != null ? projectionFilm.getFilm().getId() : null)
                .filmTitre(projectionFilm.getFilm() != null ? projectionFilm.getFilm().getTitre() : null)
                .villeId(projectionFilm.getSalle() != null
                        && projectionFilm.getSalle().getCinema() != null
                        && projectionFilm.getSalle().getCinema().getVille() != null
                        ? projectionFilm.getSalle().getCinema().getVille().getId() : null)
                .villeNom(projectionFilm.getSalle() != null
                        && projectionFilm.getSalle().getCinema() != null
                        && projectionFilm.getSalle().getCinema().getVille() != null
                        ? projectionFilm.getSalle().getCinema().getVille().getNom() : null)
                .cinemaId(projectionFilm.getSalle() != null && projectionFilm.getSalle().getCinema() != null
                        ? projectionFilm.getSalle().getCinema().getId() : null)
                .cinemaNom(projectionFilm.getSalle() != null && projectionFilm.getSalle().getCinema() != null
                        ? projectionFilm.getSalle().getCinema().getName() : null)
                .salleId(projectionFilm.getSalle() != null ? projectionFilm.getSalle().getId() : null)
                .salleNom(projectionFilm.getSalle() != null ? projectionFilm.getSalle().getName() : null)
                .build();
    }

    private AvailablePlaceDTO toAvailablePlaceDto(AvailablePlaceProjection availablePlaceProjection) {
        return AvailablePlaceDTO.builder()
                .placeId(availablePlaceProjection.getPlaceId())
                .numero(availablePlaceProjection.getNumero())
                .salleId(availablePlaceProjection.getSalleId())
                .salleName(availablePlaceProjection.getSalleName())
                .build();
    }
}
