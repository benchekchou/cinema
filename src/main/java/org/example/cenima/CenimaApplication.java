package org.example.cenima;

import org.example.cenima.Service.ICinemaInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CenimaApplication implements CommandLineRunner {
	@Autowired private ICinemaInitService cinemaInitService;
	@Value("${app.seed.enabled:false}")
	private boolean seedEnabled;

	public static void main(String[] args) {
		SpringApplication.run(CenimaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!seedEnabled) {
			return;
		}
		cinemaInitService.initVilles();
		cinemaInitService.initCinema();
		cinemaInitService.initSalles();
		cinemaInitService.initPlaces();
		cinemaInitService.initSeances();
		cinemaInitService.initCategories();
		cinemaInitService.initFilms();
		cinemaInitService.initProjectionFilms();
		cinemaInitService.initTickets();

	}
}
