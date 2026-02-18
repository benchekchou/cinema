package org.example.cenima.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.example.cenima.dto.ReservationRequestDTO;
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
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReservationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;

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

    @BeforeEach
    void ensurePaymentSequenceExists() {
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS ticket_payment_code_seq START WITH 100000 INCREMENT BY 1")
                .executeUpdate();
    }

    @Test
    @WithMockUser(username = "client@cinema.local", roles = "CLIENT")
    void reserve_shouldPreventDoubleReservation_forSamePlaceAndProjection() throws Exception {
        ReservationFixture fixture = createReservationFixture();
        ReservationRequestDTO request = ReservationRequestDTO.builder()
                .nomClient("Hamza Benchekchou")
                .placeId(fixture.placeId())
                .projectionId(fixture.projectionId())
                .build();

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placeId").value(fixture.placeId()))
                .andExpect(jsonPath("$.projectionId").value(fixture.projectionId()))
                .andExpect(jsonPath("$.reservee").value(true))
                .andExpect(jsonPath("$.codePayement").isNumber());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Place deja reservee")));

        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(1);
        assertThat(tickets.get(0).isReservee()).isTrue();
        assertThat(tickets.get(0).getNomClient()).isEqualTo("Hamza Benchekchou");
    }

    private ReservationFixture createReservationFixture() {
        Ville ville = new Ville();
        ville.setNom("Casablanca");
        ville.setLongitude(-7.6);
        ville.setLatitude(33.6);
        ville = villeRepository.save(ville);

        Cinema cinema = new Cinema();
        cinema.setName("MegaRama");
        cinema.setLongitude(-7.6);
        cinema.setAtitude(33.6);
        cinema.setNombreSalles(8);
        cinema.setVille(ville);
        cinema = cinemaRepository.save(cinema);

        Salle salle = new Salle();
        salle.setName("Salle 1");
        salle.setNombrePlace(120);
        salle.setCinema(cinema);
        salle = salleRepository.save(salle);

        Place place = new Place();
        place.setNumero(8);
        place.setLongitude(-7.6);
        place.setLatitude(33.6);
        place.setAltitude(0.0);
        place.setSalle(salle);
        place = placeRepository.save(place);

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

        return new ReservationFixture(place.getId(), projectionFilm.getId());
    }

    private record ReservationFixture(Long placeId, Long projectionId) {
    }
}
