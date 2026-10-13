package es.safareyes.educago.service.impl;

import es.safareyes.educago.dto.response.NotificacionResponse;
import es.safareyes.educago.exception.AccesoDenegadoException;
import es.safareyes.educago.exception.RecursoNoEncontradoException;
import es.safareyes.educago.modelo.*;
import es.safareyes.educago.repositorios.IEventoRepository;
import es.safareyes.educago.repositorios.IInscripcionRepository;
import es.safareyes.educago.repositorios.INotificacionRepository;
import es.safareyes.educago.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements INotificacionService {

    private final INotificacionRepository notificacionRepository;
    private final IEventoRepository eventoRepository;
    private final IInscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerMisNotificaciones(Usuario usuario) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByCreadaEnDesc(usuario.getId());

        return notificaciones.stream()
                .map(n -> new NotificacionResponse(
                        n.getId(),
                        n.getMensaje(),
                        n.getTipo().name(),
                        n.getRecurso(),
                        n.getRecursoId(),
                        n.getLeida(),
                        n.getCreadaEn()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void marcarComoLeida(Long id, Usuario usuario) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con ID: " + id));

        if (!notificacion.getUsuario().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("No tienes permiso para modificar esta notificación.");
        }

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    @Override
    @Transactional
    public void enviarRecordatorios24Horas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDe24Horas = ahora.plusHours(24);
        LocalDateTime margenSuperior = dentroDe24Horas.plusMinutes(30);

        log.info("Ejecutando tarea programada: Buscando eventos que inician entre {} y {}", dentroDe24Horas, margenSuperior);

        List<Evento> eventosProximos = eventoRepository.findAll().stream()
                .filter(e -> e.getEstado() == EstadoEvento.PUBLICADO)
                .filter(e -> e.getInicio().isAfter(dentroDe24Horas.minusMinutes(30)) && e.getInicio().isBefore(margenSuperior))
                .toList();

        for (Evento evento : eventosProximos) {
            List<Inscripcion> inscripciones = inscripcionRepository.findByEventoId(evento.getId());

            for (Inscripcion inscripcion : inscripciones) {
                if (inscripcion.getEstado() == EstadoInscripcion.CON_PLAZA) {
                    // Evitar notificaciones duplicadas
                    boolean existeAviso = notificacionRepository.existsByUsuarioIdAndRecursoIdAndTipo(
                            inscripcion.getUsuario().getId(),
                            evento.getId(),
                            TipoNotificacion.RECORDATORIO_24H
                    );

                    if (!existeAviso) {
                        Notificacion noti = Notificacion.builder()
                                .usuario(inscripcion.getUsuario())
                                .tipo(TipoNotificacion.RECORDATORIO_24H)
                                .mensaje("Recordatorio: Mañana comienza el evento '" + evento.getTitulo() + "' a las " + evento.getInicio().toLocalTime())
                                .recurso("EVENTO")
                                .recursoId(evento.getId())
                                .leida(false)
                                .build();
                        notificacionRepository.save(noti);
                    }
                }
            }
        }
    }
}