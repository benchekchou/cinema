package org.example.cenima.DAO;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor @NoArgsConstructor @ToString @Getter @Setter
public class Ville {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nom;
    private Double longitude;
    private Double latitude;
    @OneToMany(mappedBy = "ville")
    private Collection<Cinema> cinemas= new ArrayList<>();
}
