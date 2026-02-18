package org.example.cenima.integration;

import org.example.cenima.entity.Categorie;
import org.example.cenima.entity.Cinema;
import org.example.cenima.entity.Film;
import org.example.cenima.entity.Place;
import org.example.cenima.entity.ProjectionFilm;
import org.example.cenima.entity.Salle;
import org.example.cenima.entity.Seance;
import org.example.cenima.entity.Ticket;
import org.example.cenima.entity.Ville;
import org.example.cenima.repository.CategorieRepository;
import org.example.cenima.repository.CinemaRepository;
import org.example.cenima.repository.FilmRepository;
import org.example.cenima.repository.PlaceRepository;
import org.example.cenima.repository.ProjectionFilmRepository;
import org.example.cenima.repository.SalleRepository;
import org.example.cenima.repository.SeanceRepository;
import org.example.cenima.repository.TicketRepository;
import org.example.cenima.repository.VilleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SearchApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VilleRepository villeRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ProjectionFilmRepository projectionFilmRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @WithMockUser(username = "client@cinema.local", roles = "CLIENT")
    void searchProjections_shouldReturnPagedResults_whenFilteredByVille() throws Exception {
        SearchFixture fixture = createSearchFixture();

        mockMvc.perform(get("/api/v1/search/projections")
                        .param("villeId", fixture.ville().getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].projectionId").value(fixture.projection().getId()))
                .andExpect(jsonPath("$.content[0].cinemaNom").value("MegaRama"));
    }

    @Test
    @WithMockUser(username = "client@cinema.local", roles = "CLIENT")
    void searchAvailablePlaces_shouldExcludeAlreadyReservedSeats() throws Exception {
        SearchFixture fixture = createSearchFixture();

        Ticket reservedTicket = new Ticket();
        reservedTicket.setNomClient("Client One");
        reservedTicket.setPrix(fixture.projection().getPrix());
        reservedTicket.setCodePayement(100001);
        reservedTicket.setReservee(true);
        reservedTicket.setPlace(fixture.place1());
        reservedTicket.setProjectionFilm(fixture.projection());
        ticketRepository.save(reservedTicket);

        mockMvc.perform(get("/api/v1/search/projections/{projectionId}/available-places", fixture.projection().getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].placeId").value(fixture.place2().getId()));
    }

    private SearchFixture createSearchFixture() {
        Ville ville = new Ville();
        ville.setNom("Casablanca");
        ville.setLongitude(-7.6);
        ville.setLatitude(33.6);
        ville = villeRepository.save(ville);

        Cinema cinema = new Cinema();
        cinema.setName("MegaRama");
        cinema.setLongitude(-7.6);
        cinema.setAtitude(33.6);
        cinema.setNombreSalles(6);
        cinema.setVille(ville);
        cinema = cinemaRepository.save(cinema);

        Salle salle = new Salle();
        salle.setName("Salle 2");
        salle.setNombrePlace(100);
        salle.setCinema(cinema);
        salle = salleRepository.save(salle);

        Place place1 = new Place();
        place1.setNumero(1);
        place1.setLongitude(-7.6);
        place1.setLatitude(33.6);
        place1.setAltitude(0.0);
        place1.setSalle(salle);
        place1 = placeRepository.save(place1);

        Place place2 = new Place();
        place2.setNumero(2);
        place2.setLongitude(-7.6);
        place2.setLatitude(33.6);
        place2.setAltitude(0.0);
        place2.setSalle(salle);
        place2 = placeRepository.save(place2);

        Seance seance = new Seance();
        seance.setHeureDebut(new Date());
        seance = seanceRepository.save(seance);

        Categorie categorie = new Categorie();
        categorie.setName("Science Fiction");
        categorie = categorieRepository.save(categorie);

        Film film = new Film();
        film.setTitre("Interstellar");
        film.setDescription("Voyage spatial");
        film.setDuree(2.49);
        film.setRealisateur("Christopher Nolan");
        film.setPhoto("interstellar.jpg");
        film.setDateSortie(new Date());
        film.setCategorie(categorie);
        film = filmRepository.save(film);

        ProjectionFilm projectionFilm = new ProjectionFilm();
        projectionFilm.setDateProjection(new Date());
        projectionFilm.setPrix(70.0);
        projectionFilm.setFilm(film);
        projectionFilm.setSalle(salle);
        projectionFilm.setSeance(seance);
        projectionFilm = projectionFilmRepository.save(projectionFilm);

        return new SearchFixture(ville, projectionFilm, place1, place2);
    }

    private record SearchFixture(Ville ville, ProjectionFilm projection, Place place1, Place place2) {
    }
}
