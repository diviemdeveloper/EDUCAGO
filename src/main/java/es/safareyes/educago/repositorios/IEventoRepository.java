package es.safareyes.educago.repositorios;

import es.safareyes.educago.dto.response.EventoCalendarioProjection;
import es.safareyes.educago.modelo.CambioEvento;
import es.safareyes.educago.modelo.Evento;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    // Q1-1: Método derivado por nombre (Búsqueda por rango de fechas)
    List<Evento> findByEstadoAndInicioBetweenOrderByInicioAsc(CambioEvento estado, LocalDateTime inicio, LocalDateTime fin);

    // Q1-2: Método derivado por nombre (Búsqueda rápida por título)
    List<Evento> findByTituloContainingIgnoreCase(String texto);

    // Q2-1: JPQL con JOIN / Parámetros (Regla RN-01: Validar solapamiento de horarios en un lugar)
    @Query("SELECT COUNT(e) FROM Evento e WHERE e.lugar.id = :lugarId AND e.estado <> 'CANCELADO' " +
            "AND (:eventoId IS NULL OR e.id <> :eventoId) AND (e.inicio < :fin AND e.fin > :inicio)")
    long countSolapamientos(@Param("lugarId") Long lugarId,
                            @Param("eventoId") Long eventoId,
                            @Param("inicio") LocalDateTime inicio,
                            @Param("fin") LocalDateTime fin);

    // Q2-2: JPQL con JOIN / MEMBER OF (Regla RN-03: Eventos autorizados para el curso de un alumno)
    @Query("SELECT e FROM Evento e WHERE e.estado = 'PUBLICADO' AND (SIZE(e.cursos) = 0 OR :cursoId MEMBER OF e.cursos)")
    List<Evento> findEventosDisponiblesParaCurso(@Param("cursoId") Long cursoId);

    // Q3-1: SQL Nativo con PostgreSQL + Q5-1: Proyección basada en interfaz (Calendario mensual)
    @Query(value = "SELECT e.id AS id, e.titulo AS titulo, e.inicio AS inicio, e.fin AS fin, " +
            "c.nombre AS nombreCategoria, c.icono AS iconoCategoria, c.color AS colorCategoria " +
            "FROM eventos e JOIN categorias c ON e.categoria_id = c.id " +
            "WHERE e.estado = 'PUBLICADO' AND EXTRACT(MONTH FROM e.inicio) = :mes " +
            "AND EXTRACT(YEAR FROM e.inicio) = :anio ORDER BY e.inicio ASC", nativeQuery = true)
    List<EventoCalendarioProjection> findEventosCalendario(@Param("mes") int mes, @Param("anio") int anio);

    // Q4: Consulta de modificación masiva (Tarea programada: pasar eventos pasados a FINALIZADO)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Evento e SET e.estado = 'FINALIZADO' WHERE e.estado = 'PUBLICADO' AND e.fin < :ahora")
    int finalizarEventosVencidos(@Param("ahora") LocalDateTime ahora);

    // Q8: Evitar problema N+1 mediante @EntityGraph
    @EntityGraph(attributePaths = {"lugar", "categoria", "cursos", "organizador"})
    @Query("SELECT e FROM Evento e WHERE e.id = :id")
    Optional<Evento> findByIdConRelaciones(@Param("id") Long id);
}