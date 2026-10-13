package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.CambioEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICambioEventoRepository extends JpaRepository<CambioEvento, Long> {

    List<CambioEvento> findByEventoIdOrderByFechaDesc(Long eventoId);
}