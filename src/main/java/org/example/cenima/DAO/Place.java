package org.example.cenima.DAO;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    @ManyToOne
    private Salle salle;
    @OneToMany(mappedBy = "place")
    private Collection<Ticket> tickets=new ArrayList<>();

}
