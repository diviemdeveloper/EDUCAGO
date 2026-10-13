package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lugares")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private Integer aforoMaximo;
}