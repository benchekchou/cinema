package org.example.cenima.DAO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Salle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int nombrePlace;
    @OneToMany(mappedBy = "salle")
    private Collection<ProjectionFilm> projectionFilms= new ArrayList<>();
    @OneToMany(mappedBy = "salle")
    private Collection<Place> places = new ArrayList<>();
    @ManyToOne
    private Cinema cinema;
}
