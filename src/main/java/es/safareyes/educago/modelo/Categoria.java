package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria", schema = "educago")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "icono")
    private String icono;

    @Column(name = "color", length = 20)
    private String color;
}
