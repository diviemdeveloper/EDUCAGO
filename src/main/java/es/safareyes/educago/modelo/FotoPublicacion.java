package es.safareyes.educago.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fotos_publicacion")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FotoPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @Column(nullable = false)
    private String url;

    private String claveDisco; // Opcional / Nombre UUID en el almacenamiento

    private String nombreOriginal;

    private String tipo;

    private Long tamano;

    private Integer orden;

    private LocalDateTime creadaEn;

    @PrePersist
    public void prePersist() {
        this.creadaEn = LocalDateTime.now();
    }
}