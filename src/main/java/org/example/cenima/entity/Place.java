package org.example.cenima.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity @AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"salle", "tickets"})
@Getter
@Setter
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;
    @OneToMany(mappedBy = "place")
    private Collection<Ticket> tickets=new ArrayList<>();

}
