package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.FotoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFotoPublicacionRepository extends JpaRepository<FotoPublicacion, Long> {

    // Q1: Contar fotos de una publicación (RN-09: Máximo 10 fotos)
    long countByPublicacionId(Long publicacionId);

    List<FotoPublicacion> findByPublicacionIdOrderByOrdenAsc(Long publicacionId);
}