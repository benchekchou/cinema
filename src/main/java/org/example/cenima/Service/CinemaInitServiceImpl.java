package org.example.cenima.Service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Service
@Transactional
public class CinemaInitServiceImpl implements ICinemaInitService {
    @Autowired private VilleRepository villeRepository;
    @Autowired private CinemaRepository cinemaRepository;
    @Autowired private SalleRepository salleRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private SeanceRepository seanceRepository;
    @Autowired private FilmRepository filmRepository;
    @Autowired private ProjectionFilmRepository projectionFilmRepository;
    @Autowired private CategorieRepository categorieRepository;
    @Autowired private TicketRepository ticketRepository;
    @Override
    public void initVilles() {
        Stream.of("Casablanca","Marrakech","Rabat","Tanger").forEach(nameVille -> {
           Ville ville = new Ville();
           ville.setNom(nameVille);
           villeRepository.save(ville);
        });

    }

    @Override
    public void initCinema() {
        villeRepository.findAll().forEach(ville -> {
           Stream.of("MegaRama","IMAX","FOUNOUN","CHAHRAZAD","DAOULIZ").forEach(nameCinema -> {
               Cinema cinema = new Cinema();
               cinema.setName(nameCinema);
               cinema.setVille(ville);
               cinema.setNombreSalles(3+(int)(Math.random()*7));
               cinemaRepository.save(cinema);
           });
        });


    }

    @Override
    public void initSalles() {
        cinemaRepository.findAll().forEach(cinema -> {
           for (int i =0; i < cinema.getNombreSalles(); i++) {
               Salle salle = new Salle();
               salle.setName("Salle"+(i+1));
               salle.setCinema(cinema);
               salle.setNombrePlace(15+(int)(Math.random()*20));
               salleRepository.save(salle);
           }
        });

    }

    @Override
    public void initPlaces() {
       salleRepository.findAll().forEach(salle -> {
           for (int i =0; i < salle.getNombrePlace(); i++) {
               Place place = new Place();
               place.setNumero(i+1);
               place.setSalle(salle);
               placeRepository.save(place);
           }
       });
    }

    @Override
    public void initSeances() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Stream.of("12:00","15:00","17:00","19:00","21:00").forEach(s -> {
           Seance seance = new Seance();
           try{
               seance.setHeureDebut(dateFormat.parse(s));
               seanceRepository.save(seance);

           }catch (ParseException e){
               e.printStackTrace();
           }
        });

    }

    @Override
    public void initCategories() {
        Stream.of("Histoire","Actions","Fiction","Drama").forEach(s -> {
            Categorie categorie = new Categorie();
            categorie.setName(s);
            categorieRepository.save(categorie);
        });
    }

    @Override
    public void initFilms() {
        double [] durees = new double[]{1,1.5,2,2.5,3};
        List<Categorie> categories = categorieRepository.findAll();
        Stream.of(" 12 Hommes en colere","Forrest Gump","Green Book","La Ligne verte","Le Parrain","Le Seigneur des anneaux").forEach(titreFilm -> {
           Film film = new Film();
           film.setTitre(titreFilm);
           film.setDuree(durees[new Random().nextInt(durees.length)]);
           film.setPhoto(titreFilm.replaceAll(" "," ")+".jpg");
           film.setCategorie(categories.get(new Random().nextInt(categories.size())));
           filmRepository.save(film);
        });

    }

    @Override
    public void initProjectionFilms() {
        double [] prices= new double[]{30,50,60,70,90,100};
        villeRepository.findAll().forEach(ville -> {
           ville.getCinemas().forEach(cinema -> {
             cinema.getSalles().forEach(salle -> {
                 filmRepository.findAll().forEach(film -> {
                     seanceRepository.findAll().forEach(seance -> {
                         ProjectionFilm projectionFilm = new ProjectionFilm();
                         projectionFilm.setDateProjection(new Date());
                         projectionFilm.setFilm(film);
                         projectionFilm.setPrix(prices[new Random().nextInt(prices.length)]);
                         projectionFilm.setSalle(salle);
                         projectionFilm.setSeance(seance);
                         projectionFilmRepository.save(projectionFilm);
                     });
                 });
             });
           });
        });

    }

    @Override
    public void initTickets() {
        projectionFilmRepository.findAll().forEach(p -> {
            p.getSalle().getPlaces().forEach(place -> {
                Ticket ticket = new Ticket();
                ticket.setPlace(place);
                ticket.setPrix(p.getPrix());
                ticket.setProjectionFilm(p);
                ticket.setReservee(false);
                ticketRepository.save(ticket);
            }) ;
        });

    }
}
