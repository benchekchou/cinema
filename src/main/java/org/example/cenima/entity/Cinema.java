package org.example.cenima.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"ville", "salles"})
@Getter
@Setter
public class Cinema {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double longitude;
    private Double atitude;
    private int nombreSalles;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ville_id", nullable = false)
    private Ville ville;
    @OneToMany(mappedBy = "cinema")
    private Collection<Salle> salles= new ArrayList<>();

}
