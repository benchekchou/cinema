package org.example.cenima.Service.impl;

import org.example.cenima.dto.FilmRequestDTO;
import org.example.cenima.dto.FilmResponseDTO;
import org.example.cenima.entity.Categorie;
import org.example.cenima.entity.Film;
import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.repository.CategorieRepository;
import org.example.cenima.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {

    @Mock
    private FilmRepository filmRepository;
    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void create_shouldPersistFilmAndReturnDto() {
        Categorie categorie = new Categorie();
        categorie.setId(3L);

        Date dateSortie = new Date();
        FilmRequestDTO request = FilmRequestDTO.builder()
                .titre("  Interstellar  ")
                .description("Voyage spatial")
                .duree(2.49)
                .realisateur("Christopher Nolan")
                .photo("interstellar.jpg")
                .dateSortie(dateSortie)
                .categorieId(3L)
                .build();

        when(categorieRepository.findById(3L)).thenReturn(Optional.of(categorie));
        when(filmRepository.save(any(Film.class))).thenAnswer(invocation -> {
            Film film = invocation.getArgument(0);
            film.setId(101L);
            return film;
        });

        FilmResponseDTO response = filmService.create(request);

        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getTitre()).isEqualTo("Interstellar");
        assertThat(response.getCategorieId()).isEqualTo(3L);
        assertThat(response.getDateSortie()).isEqualTo(dateSortie);
        verify(filmRepository).save(any(Film.class));
    }

    @Test
    void create_shouldThrowNotFound_whenCategorieDoesNotExist() {
        FilmRequestDTO request = FilmRequestDTO.builder()
                .titre("Interstellar")
                .duree(2.49)
                .categorieId(99L)
                .build();

        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> filmService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categorie not found with id 99");
    }
}
