package org.example.cenima.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cenima.dto.FilmRequestDTO;
import org.example.cenima.entity.Categorie;
import org.example.cenima.entity.Film;
import org.example.cenima.repository.CategorieRepository;
import org.example.cenima.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FilmApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private FilmRepository filmRepository;

    @Test
    @WithMockUser(username = "admin@cinema.local", roles = "ADMIN")
    void findAll_shouldReturnFilms_forAdmin() throws Exception {
        Categorie categorie = new Categorie();
        categorie.setName("Action");
        categorie = categorieRepository.save(categorie);

        Film film = new Film();
        film.setTitre("Inception");
        film.setDescription("Dream in dream");
        film.setDuree(2.28);
        film.setRealisateur("Christopher Nolan");
        film.setPhoto("inception.jpg");
        film.setDateSortie(new Date());
        film.setCategorie(categorie);
        filmRepository.save(film);

        mockMvc.perform(get("/api/v1/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value("Inception"))
                .andExpect(jsonPath("$[0].categorieId").value(categorie.getId()));
    }

    @Test
    @WithMockUser(username = "admin@cinema.local", roles = "ADMIN")
    void create_shouldPersistFilm_andReturnCreated() throws Exception {
        Categorie categorie = new Categorie();
        categorie.setName("Science Fiction");
        categorie = categorieRepository.save(categorie);

        FilmRequestDTO request = FilmRequestDTO.builder()
                .titre("Interstellar")
                .description("Voyage spatial")
                .duree(2.49)
                .realisateur("Christopher Nolan")
                .photo("interstellar.jpg")
                .dateSortie(new Date())
                .categorieId(categorie.getId())
                .build();

        mockMvc.perform(post("/api/v1/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titre").value("Interstellar"))
                .andExpect(jsonPath("$.categorieId").value(categorie.getId()));

        assertThat(filmRepository.count()).isEqualTo(1L);
    }
}
