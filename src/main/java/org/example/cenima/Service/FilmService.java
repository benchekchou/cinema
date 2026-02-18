package org.example.cenima.Service;

import org.example.cenima.dto.FilmRequestDTO;
import org.example.cenima.dto.FilmResponseDTO;

import java.util.List;

public interface FilmService {
    List<FilmResponseDTO> findAll();

    FilmResponseDTO create(FilmRequestDTO request);

    void delete(Long id);
}
