package es.safareyes.educago.repositorios;

import es.safareyes.educago.modelo.Notificacion;
import es.safareyes.educago.modelo.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface INotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioIdOrderByCreadaEnDesc(Long usuarioId);

    boolean existsByUsuarioIdAndRecursoIdAndTipo(Long usuarioId, Long recursoId, TipoNotificacion tipo);
}