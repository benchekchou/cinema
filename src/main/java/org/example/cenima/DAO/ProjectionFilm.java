package org.example.cenima.DAO;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
@Entity @AllArgsConstructor @NoArgsConstructor
@Builder @Getter
@Setter
public class ProjectionFilm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateProjection;
    private Double prix;
    @ManyToOne
    private Film film;
    @ManyToOne
    private Salle salle;
    @OneToMany
    private Collection<Ticket> tickets = new ArrayList<>();
    @ManyToOne
    private Seance seance;

}
