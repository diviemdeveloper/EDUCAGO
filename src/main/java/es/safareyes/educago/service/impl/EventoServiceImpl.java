package es.safareyes.educago.service.impl;

import es.safareyes.educago.dto.request.CambiarEstadoEventoRequest;
import es.safareyes.educago.dto.request.CrearEventoRequest;
import es.safareyes.educago.dto.response.EventoCalendarioProjection;
import es.safareyes.educago.dto.response.EventoResponse;
import es.safareyes.educago.exception.AccesoDenegadoException;
import es.safareyes.educago.exception.ConflictoException;
import es.safareyes.educago.exception.RecursoNoEncontradoException;
import es.safareyes.educago.exception.ReglaNegocioException;
import es.safareyes.educago.mapper.IEventoMapper;
import es.safareyes.educago.modelo.*;
import es.safareyes.educago.repositorios.*;
import es.safareyes.educago.service.IEventoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventoServiceImpl implements IEventoService {

    private final IEventoRepository eventoRepository;
    private final ILugarRepository lugarRepository;
    private final ICategoriaRepository categoriaRepository;
    private final ICursoRepository cursoRepository;
    private final IInscripcionRepository inscripcionRepository;
    private final ICambioEventoRepository cambioEventoRepository;
    private final IEventoMapper eventoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<EventoResponse> buscarEventos(String categoria, Long cursoId, String texto, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        return eventoRepository.findAll(pageable)
                .map(this::enriquecerRespuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoCalendarioProjection> obtenerCalendario(int mes, int anio) {
        return eventoRepository.findEventosCalendario(mes, anio);
    }

    @Override
    @Transactional(readOnly = true)
    public EventoResponse obtenerDetalle(Long id) {
        Evento evento = eventoRepository.findByIdConRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + id));
        return enriquecerRespuesta(evento);
    }

    @Override
    @Transactional
    public EventoResponse crearEvento(CrearEventoRequest request, Usuario organizador) {
        log.info("Creando nuevo evento en BORRADOR: {}", request.titulo());

        if (request.fin().isBefore(request.inicio())) {
            throw new ReglaNegocioException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }
        if (request.fechaLimiteInscripcion().isAfter(request.inicio())) {
            throw new ReglaNegocioException("La fecha límite de inscripción debe ser anterior o igual al inicio.");
        }

        Lugar lugar = lugarRepository.findById(request.lugarId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lugar no encontrado con ID: " + request.lugarId()));

        if (request.aforoMaximo() > lugar.getAforoMaximo()) {
            throw new ReglaNegocioException("El aforo del evento excede el aforo máximo permitido del lugar (" + lugar.getAforoMaximo() + ").");
        }

        long solapamientos = eventoRepository.countSolapamientos(lugar.getId(), null, request.inicio(), request.fin());
        if (solapamientos > 0) {
            throw new ConflictoException("Ya existe otro evento no cancelado programado en ese lugar durante el mismo horario.");
        }

        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + request.categoriaId()));

        Set<Curso> cursos = new HashSet<>();
        if (request.cursosIds() != null && !request.cursosIds().isEmpty()) {
            cursos.addAll(cursoRepository.findAllById(request.cursosIds()));
        }

        Evento evento = Evento.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .requisitos(request.requisitos())
                .inicio(request.inicio())
                .fin(request.fin())
                .estado(EstadoEvento.BORRADOR) // Nace siempre en BORRADOR (RN-02)
                .aforoMaximo(request.aforoMaximo())
                .fechaLimiteInscripcion(request.fechaLimiteInscripcion())
                .lugar(lugar)
                .categoria(categoria)
                .organizador(organizador)
                .cursos(cursos)
                .build();

        Evento guardado = eventoRepository.save(evento);
        return enriquecerRespuesta(guardado);
    }

    @Override
    @Transactional
    public EventoResponse editarEvento(Long id, CrearEventoRequest request, Usuario autor) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + id));

        validarAutoridad(evento, autor);

        if (evento.getEstado() == EstadoEvento.PUBLICADO) {
            if (!evento.getInicio().equals(request.inicio())) {
                registrarCambio(evento, "inicio", evento.getInicio().toString(), request.inicio().toString(), autor);
            }
            if (!evento.getLugar().getId().equals(request.lugarId())) {
                registrarCambio(evento, "lugar", evento.getLugar().getNombre(), request.lugarId().toString(), autor);
            }
        }

        Lugar lugar = lugarRepository.findById(request.lugarId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lugar no encontrado"));

        evento.setTitulo(request.titulo());
        evento.setDescripcion(request.descripcion());
        evento.setRequisitos(request.requisitos());
        evento.setInicio(request.inicio());
        evento.setFin(request.fin());
        evento.setAforoMaximo(request.aforoMaximo());
        evento.setFechaLimiteInscripcion(request.fechaLimiteInscripcion());
        evento.setLugar(lugar);

        return enriquecerRespuesta(eventoRepository.save(evento));
    }

    @Override
    @Transactional
    public EventoResponse cambiarEstado(Long id, CambiarEstadoEventoRequest request, Usuario autor) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + id));

        validarAutoridad(evento, autor);

        evento.setEstado(request.estado());
        return enriquecerRespuesta(eventoRepository.save(evento));
    }

    private void validarAutoridad(Evento evento, Usuario usuario) {
        if (usuario.getRol() != Rol.ADMIN && !evento.getOrganizador().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("No tienes permisos para modificar este evento.");
        }
    }

    private void registrarCambio(Evento evento, String campo, String anterior, String nuevo, Usuario autor) {
        CambioEvento cambio = CambioEvento.builder()
                .evento(evento)
                .campo(campo)
                .valorAnterior(anterior)
                .valorNuevo(nuevo)
                .autor(autor)
                .build();
        cambioEventoRepository.save(cambio);
    }

    private EventoResponse enriquecerRespuesta(Evento evento) {
        long ocupadas = inscripcionRepository.countByEventoIdAndTipoAndEstado(
                evento.getId(), TipoInscripcion.ASISTIRE, EstadoInscripcion.CON_PLAZA
        );
        long plazasLibres = Math.max(0, evento.getAforoMaximo() - ocupadas);

        EventoResponse base = eventoMapper.toResponse(evento);
        return new EventoResponse(
                base.id(), base.titulo(), base.descripcion(), base.requisitos(),
                base.inicio(), base.fin(), base.estado(), base.aforoMaximo(),
                base.fechaLimiteInscripcion(), base.nombreLugar(), base.nombreCategoria(),
                base.iconoCategoria(), base.colorCategoria(), base.organizadorId(),
                base.nombreOrganizador(), base.cursosPermitidos(), plazasLibres
        );
    }
}