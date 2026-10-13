package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.EstadoInscripcion;
import es.safareyes.educago.modelo.Inscripcion;
import es.safareyes.educago.modelo.TipoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IInscripcionRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

    Optional<Inscripcion> findByEventoIdAndUsuarioId(Long eventoId, Long usuarioId);

    List<Inscripcion> findByEventoId(Long eventoId);

    long countByEventoIdAndTipoAndEstado(Long eventoId, TipoInscripcion tipo, EstadoInscripcion estado);

    Optional<Inscripcion> findTopByEventoIdAndTipoAndEstadoOrderByPosicionEsperaAsc(
            Long eventoId, TipoInscripcion tipo, EstadoInscripcion estado
    );

    // RN-04: Solapamiento de horarios para un usuario con plaza confirmada (Q5)
    @Query("""
        SELECT COUNT(i) FROM Inscripcion i
        WHERE i.usuario.id = :usuarioId
          AND i.tipo = es.safareyes.educago.modelo.TipoInscripcion.ASISTIRE
          AND i.estado = es.safareyes.educago.modelo.EstadoInscripcion.CON_PLAZA
          AND i.evento.estado != es.safareyes.educago.modelo.EstadoEvento.CANCELADO
          AND i.evento.inicio < :fin
          AND i.evento.fin > :inicio
    """)
    long countSolapamientosAsistenciaUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // RN-05: Reordenar lista de espera decrementando 1 posición a todos los que estaban esperando
    @Modifying
    @Query("""
        UPDATE Inscripcion i
        SET i.posicionEspera = i.posicionEspera - 1
        WHERE i.evento.id = :eventoId
          AND i.estado = es.safareyes.educago.modelo.EstadoInscripcion.EN_ESPERA
          AND i.posicionEspera IS NOT NULL
    """)
    void decrementarPosicionesListaEspera(@Param("eventoId") Long eventoId);
}