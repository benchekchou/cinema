package org.example.cenima.Service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cenima.Service.FilmService;
import org.example.cenima.dto.FilmRequestDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.example.cenima.entity.Categorie;
import org.example.cenima.entity.Film;
import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.repository.CategorieRepository;
import org.example.cenima.repository.FilmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final CategorieRepository categorieRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FilmResponseDTO> findAll() {
        return filmRepository.findAllWithCategorie()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public FilmResponseDTO create(FilmRequestDTO request) {
        Categorie categorie = categorieRepository.findById(request.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Categorie not found with id " + request.getCategorieId()));

        Film film = new Film();
        film.setTitre(request.getTitre().trim());
        film.setDescription(request.getDescription());
        film.setDuree(request.getDuree());
        film.setRealisateur(request.getRealisateur());
        film.setPhoto(request.getPhoto());
        film.setDateSortie(request.getDateSortie());
        film.setCategorie(categorie);

        Film savedFilm = filmRepository.save(film);
        return toDto(savedFilm);
    }

    @Override
    public void delete(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new ResourceNotFoundException("Film not found with id " + id);
        }
        filmRepository.deleteById(id);
    }

    private FilmResponseDTO toDto(Film film) {
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
}
