package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "evento_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoInscripcion tipo; // ME_INTERESA o ASISTIRE

    @Enumerated(EnumType.STRING)
    private EstadoInscripcion estado; // CON_PLAZA o EN_ESPERA (solo si tipo = ASISTIRE)

    private Integer posicionEspera;

    @Column(nullable = false)
    private Boolean asistio;

    private LocalDateTime creadaEn;

    @PrePersist
    public void prePersist() {
        this.creadaEn = LocalDateTime.now();
        if (this.asistio == null) this.asistio = false;
    }
}