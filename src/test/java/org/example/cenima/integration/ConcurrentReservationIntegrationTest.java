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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConcurrentReservationIntegrationTest {

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
    void setup() {
        ticketRepository.deleteAll();
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS ticket_payment_code_seq START WITH 100000 INCREMENT BY 1")
                .executeUpdate();
    }

    @Test
    void concurrentReservations_shouldResultInExactlyOneSuccess() throws Exception {
        // Setup test data
        Ville ville = new Ville();
        ville.setNom("Concurrent City");
        ville.setLongitude(0.0);
        ville.setLatitude(0.0);
        ville = villeRepository.save(ville);

        Cinema cinema = new Cinema();
        cinema.setName("ConcurrentCinema");
        cinema.setLongitude(0.0);
        cinema.setAtitude(0.0);
        cinema.setNombreSalles(1);
        cinema.setVille(ville);
        cinema = cinemaRepository.save(cinema);

        Salle salle = new Salle();
        salle.setName("SalleConc");
        salle.setNombrePlace(10);
        salle.setCinema(cinema);
        salle = salleRepository.save(salle);

        Place place = new Place();
        place.setNumero(1);
        place.setLongitude(0.0);
        place.setLatitude(0.0);
        place.setAltitude(0.0);
        place.setSalle(salle);
        place = placeRepository.save(place);

        Seance seance = new Seance();
        seance.setHeureDebut(new Date());
        seance = seanceRepository.save(seance);

        Categorie categorie = new Categorie();
        categorie.setName("ConcurrentCat");
        categorie = categorieRepository.save(categorie);

        Film film = new Film();
        film.setTitre("ConcurrentFilm");
        film.setDescription("test");
        film.setDuree(2.0);
        film.setRealisateur("Test Director");
        film.setPhoto("test.jpg");
        film.setDateSortie(new Date());
        film.setCategorie(categorie);
        film = filmRepository.save(film);

        ProjectionFilm projection = new ProjectionFilm();
        projection.setDateProjection(new Date());
        projection.setPrix(50.0);
        projection.setFilm(film);
        projection.setSalle(salle);
        projection.setSeance(seance);
        projection = projectionFilmRepository.save(projection);

        entityManager.flush();
        entityManager.clear();

        final Long placeId = place.getId();
        final Long projectionId = projection.getId();
        final int numThreads = 5;

        ReservationRequestDTO request = ReservationRequestDTO.builder()
                .nomClient("Concurrent Client")
                .placeId(placeId)
                .projectionId(projectionId)
                .build();
        String requestBody = objectMapper.writeValueAsString(request);

        // Run concurrent requests
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Future<?>[] futures = new Future[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final String clientName = "Client" + i;
            futures[i] = executor.submit(() -> {
                try {
                    latch.await();
                    MvcResult result = mockMvc.perform(post("/api/reservations")
                                    .with(user(clientName + "@cinema.local").roles("CLIENT"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                            .andReturn();

                    int status = result.getResponse().getStatus();
                    if (status == 201) {
                        successCount.incrementAndGet();
                    } else if (status == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Count as neither success nor conflict
                }
            });
        }

        latch.countDown(); // Release all threads simultaneously
        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        // Exactly 1 should succeed, the rest should get 409
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(conflictCount.get()).isEqualTo(numThreads - 1);
    }
}
