package org.example.cenima;

import org.example.cenima.DAO.Cinema;
import org.example.cenima.DAO.Film;
import org.example.cenima.Service.ICinemaInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class CenimaApplication implements CommandLineRunner {
	@Autowired private ICinemaInitService cinemaInitService;
	@Autowired private RepositoryRestConfiguration restConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(CenimaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		restConfiguration.exposeIdsFor(Film.class);
		cinemaInitService.initCinema();cinemaInitService.initPlaces();
		cinemaInitService.initVilles();cinemaInitService.initCategories();
		cinemaInitService.initSalles();cinemaInitService.initProjectionFilms();
		cinemaInitService.initSeances();
		cinemaInitService.initFilms();
		cinemaInitService.initTickets();

	}
}
