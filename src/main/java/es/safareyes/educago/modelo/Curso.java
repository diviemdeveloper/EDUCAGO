package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cursos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}