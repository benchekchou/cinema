package org.example.cenima.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"place", "projectionFilm"})
@Getter
@Setter
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomClient;
    private Double prix;
    private int  codePayement;
    private  boolean reservee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projection_id", nullable = false)
    private ProjectionFilm projectionFilm;
}
