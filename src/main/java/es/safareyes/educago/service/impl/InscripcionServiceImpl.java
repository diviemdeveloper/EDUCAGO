package es.safareyes.educago.service.impl;

import es.safareyes.educago.dto.request.CrearInscripcionRequest;
import es.safareyes.educago.dto.request.MarcarAsistenciaRequest;
import es.safareyes.educago.dto.response.InscripcionResponse;
import es.safareyes.educago.exception.AccesoDenegadoException;
import es.safareyes.educago.exception.ConflictoException;
import es.safareyes.educago.exception.RecursoNoEncontradoException;
import es.safareyes.educago.exception.ReglaNegocioException;
import es.safareyes.educago.mapper.IInscripcionMapper;
import es.safareyes.educago.modelo.*;
import es.safareyes.educago.repositorios.*;
import es.safareyes.educago.service.IInscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscripcionServiceImpl implements IInscripcionService {

    private final IInscripcionRepository inscripcionRepository;
    private final IEventoRepository eventoRepository;
    private final INotificacionRepository notificacionRepository;
    private final IInscripcionMapper inscripcionMapper;

    @Override
    @Transactional
    public InscripcionResponse inscribir(Long eventoId, CrearInscripcionRequest request, Usuario usuario) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + eventoId));

        // Solo se permite la inscripción en eventos PUBLICADOS
        if (evento.getEstado() != EstadoEvento.PUBLICADO) {
            throw new ReglaNegocioException("Solo es posible inscribirse en eventos en estado PUBLICADO.");
        }

        // Comprobar fecha límite de inscripción
        if (LocalDateTime.now().isAfter(evento.getFechaLimiteInscripcion())) {
            throw new ReglaNegocioException("El plazo de inscripción para este evento ha finalizado.");
        }

        // RN-03: Restricción por curso
        if (evento.getCursos() != null && !evento.getCursos().isEmpty()) {
            if (usuario.getCurso() == null || !evento.getCursos().contains(usuario.getCurso())) {
                throw new ReglaNegocioException("No perteneces a ninguno de los cursos autorizados para este evento.");
            }
        }

        // Comprobar si ya existe inscripción previa
        if (inscripcionRepository.existsByEventoIdAndUsuarioId(eventoId, usuario.getId())) {
            throw new ConflictoException("Ya te encuentras inscrito en este evento.");
        }

        EstadoInscripcion estadoFinal = EstadoInscripcion.CON_PLAZA;
        Integer posicionEspera = null;

        if (request.tipo() == TipoInscripcion.ASISTIRE) {
            // RN-04: Comprobar solapamiento de horarios con otros eventos con plaza confirmada
            long solapamientos = inscripcionRepository.countSolapamientosAsistenciaUsuario(
                    usuario.getId(), evento.getInicio(), evento.getFin()
            );
            if (solapamientos > 0) {
                throw new ConflictoException("Ya tienes asistencia confirmada a otro evento programado en el mismo horario.");
            }

            // RN-05: Comprobar aforo máximo
            long ocupadas = inscripcionRepository.countByEventoIdAndTipoAndEstado(
                    eventoId, TipoInscripcion.ASISTIRE, EstadoInscripcion.CON_PLAZA
            );

            if (ocupadas >= evento.getAforoMaximo()) {
                // Pasa a lista de espera
                estadoFinal = EstadoInscripcion.EN_ESPERA;
                long enEspera = inscripcionRepository.countByEventoIdAndTipoAndEstado(
                        eventoId, TipoInscripcion.ASISTIRE, EstadoInscripcion.EN_ESPERA
                );
                posicionEspera = (int) enEspera + 1;
            }
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .evento(evento)
                .usuario(usuario)
                .tipo(request.tipo())
                .estado(estadoFinal)
                .posicionEspera(posicionEspera)
                .asistio(false)
                .build();

        Inscripcion guardada = inscripcionRepository.save(inscripcion);
        return inscripcionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void cancelarInscripcion(Long eventoId, Usuario usuario) {
        Inscripcion inscripcion = inscripcionRepository.findByEventoIdAndUsuarioId(eventoId, usuario.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No tienes ninguna inscripción activa en este evento."));

        boolean teniaPlaza = (inscripcion.getTipo() == TipoInscripcion.ASISTIRE && inscripcion.getEstado() == EstadoInscripcion.CON_PLAZA);

        inscripcionRepository.delete(inscripcion);

        // RN-05: Si la persona liberó una plaza, se promociona al primero en lista de espera
        if (teniaPlaza) {
            promocionarSiguienteListaEspera(eventoId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerInscripcionesEvento(Long eventoId, Usuario usuario) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado"));

        validarOrganizadorOAdmin(evento, usuario);

        return inscripcionRepository.findByEventoId(eventoId).stream()
                .map(inscripcionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InscripcionResponse marcarAsistencia(Long eventoId, Long inscripcionId, MarcarAsistenciaRequest request, Usuario organizador) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado"));

        validarOrganizadorOAdmin(evento, organizador);

        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));

        inscripcion.setAsistio(request.asistio());
        return inscripcionMapper.toResponse(inscripcionRepository.save(inscripcion));
    }

    private void promocionarSiguienteListaEspera(Long eventoId) {
        Optional<Inscripcion> primeroEnEspera = inscripcionRepository
                .findTopByEventoIdAndTipoAndEstadoOrderByPosicionEsperaAsc(
                        eventoId, TipoInscripcion.ASISTIRE, EstadoInscripcion.EN_ESPERA
                );

        if (primeroEnEspera.isPresent()) {
            Inscripcion promovido = primeroEnEspera.get();
            promovido.setEstado(EstadoInscripcion.CON_PLAZA);
            promovido.setPosicionEspera(null);
            inscripcionRepository.save(promovido);

            // Reordenar las posiciones de los demás en lista de espera
            inscripcionRepository.decrementarPosicionesListaEspera(eventoId);

            // Generar notificación al alumno promocionado (RN-05)
            Notificacion notificacion = Notificacion.builder()
                    .usuario(promovido.getUsuario())
                    .tipo(TipoNotificacion.CAMBIO_ESTADO_EVENTO)
                    .mensaje("¡Buenas noticias! Has obtenido plaza confirmada para el evento: " + promovido.getEvento().getTitulo())
                    .recurso("EVENTO")
                    .recursoId(eventoId)
                    .leida(false)
                    .build();
            notificacionRepository.save(notificacion);
        }
    }

    private void validarOrganizadorOAdmin(Evento evento, Usuario usuario) {
        if (usuario.getRol() != Rol.ADMIN && !evento.getOrganizador().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("No tienes permisos para gestionar inscripciones en este evento.");
        }
    }
}