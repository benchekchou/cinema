package org.example.cenima.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor @NoArgsConstructor @ToString(exclude = "cinemas") @Getter @Setter
public class Ville {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nom;
    private Double longitude;
    private Double latitude;
    @OneToMany(mappedBy = "ville")
    private Collection<Cinema> cinemas= new ArrayList<>();
}
