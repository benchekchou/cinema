package org.example.cenima.Service.impl;

import org.example.cenima.exception.ResourceNotFoundException;
import org.example.cenima.repository.FilmRepository;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private FilmRepository filmRepository;
    @Mock
    private ProjectionFilmRepository projectionFilmRepository;
    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchProjections_shouldFail_whenNoFilterProvided() {
        assertThatThrownBy(() -> searchService.searchProjections(null, null, null, PageRequest.of(0, 20)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one filter is required");
    }

    @Test
    void searchAvailablePlaces_shouldFail_whenProjectionDoesNotExist() {
        when(projectionFilmRepository.existsById(45L)).thenReturn(false);

        assertThatThrownBy(() -> searchService.searchAvailablePlaces(45L, PageRequest.of(0, 20)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Projection not found with id 45");
    }
}
