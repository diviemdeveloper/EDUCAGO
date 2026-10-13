package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @Column(nullable = false)
    private String mensaje;

    private String recurso; // "EVENTO", "PUBLICACION"
    private Long recursoId;

    @Column(nullable = false)
    private Boolean leida;

    private LocalDateTime creadaEn;

    @PrePersist
    public void prePersist() {
        this.creadaEn = LocalDateTime.now();
        if (this.leida == null) this.leida = false;
    }
}