package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Publicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPublicacionRepository extends JpaRepository<Publicacion, Long> {

    // Versión lista completa
    List<Publicacion> findByEventoIdOrderByCreadaEnDesc(Long eventoId);

    // Q6: Paginación de publicaciones de un evento
    Page<Publicacion> findByEventoIdOrderByCreadaEnDesc(Long eventoId, Pageable pageable);

    // Q8: Cargar publicación con sus fotos asociadas en una sola consulta
    @EntityGraph(attributePaths = {"fotos", "autor"})
    @Query("SELECT p FROM Publicacion p WHERE p.id = :id")
    Optional<Publicacion> findByIdConFotos(@Param("id") Long id);
}