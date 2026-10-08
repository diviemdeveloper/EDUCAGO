package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lugar", schema = "educago")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Lugar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "aforo_max")
    private Integer aforo_max;
}
