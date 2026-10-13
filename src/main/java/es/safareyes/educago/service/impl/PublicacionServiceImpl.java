package es.safareyes.educago.service.impl;

import es.safareyes.educago.dto.request.CrearPublicacionRequest;
import es.safareyes.educago.dto.response.PublicacionResponse;
import es.safareyes.educago.exception.AccesoDenegadoException;
import es.safareyes.educago.exception.RecursoNoEncontradoException;
import es.safareyes.educago.exception.ReglaNegocioException;
import es.safareyes.educago.modelo.*;
import es.safareyes.educago.repositorios.*;
import es.safareyes.educago.service.IPublicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicacionServiceImpl implements IPublicacionService {

    private final IPublicacionRepository publicacionRepository;
    private final IFotoPublicacionRepository fotoPublicacionRepository;
    private final IEventoRepository eventoRepository;
    private final IInscripcionRepository inscripcionRepository;
    private final INotificacionRepository notificacionRepository;

    @Override
    @Transactional
    public PublicacionResponse crearPublicacion(Long eventoId, CrearPublicacionRequest request, Usuario autor) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + eventoId));

        // Validar permisos (Solo Organizador del evento o ADMIN)
        validarOrganizadorOAdmin(evento, autor);

        // RN-08: Solo se pueden realizar publicaciones en eventos en estado FINALIZADO
        if (evento.getEstado() != EstadoEvento.FINALIZADO) {
            throw new ReglaNegocioException("Solo se pueden crear publicaciones para eventos que hayan FINALIZADO.");
        }

        Publicacion publicacion = Publicacion.builder()
                .evento(evento)
                .autor(autor)
                .texto(request.texto())
                .build();

        Publicacion guardada = publicacionRepository.save(publicacion);

        List<String> fotosGuardadas = new ArrayList<>();
        if (request.urlsFotos() != null && !request.urlsFotos().isEmpty()) {
            for (String url : request.urlsFotos()) {
                FotoPublicacion foto = FotoPublicacion.builder()
                        .publicacion(guardada)
                        .url(url)
                        .build();
                fotoPublicacionRepository.save(foto);
                fotosGuardadas.add(url);
            }
        }

        // Notificar a todos los asistentes confirmados que hay nuevas fotos/resumen
        notificarAsistentesNuevaPublicacion(evento);

        return new PublicacionResponse(
                guardada.getId(),
                evento.getId(),
                autor.getId(),
                autor.getNombre() + " " + autor.getApellidos(),
                guardada.getTexto(),
                fotosGuardadas,
                guardada.getCreadaEn()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicacionResponse> obtenerPublicacionesEvento(Long eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new RecursoNoEncontradoException("Evento no encontrado con ID: " + eventoId);
        }

        List<Publicacion> publicaciones = publicacionRepository.findByEventoIdOrderByCreadaEnDesc(eventoId);

        return publicaciones.stream().map(p -> {
            List<String> fotos = fotoPublicacionRepository.findByPublicacionIdOrderByOrdenAsc(p.getId())
                    .stream()
                    .map(foto -> foto.getUrl())
                    .toList();

            return new PublicacionResponse(
                    p.getId(),
                    p.getEvento().getId(),
                    p.getAutor().getId(),
                    p.getAutor().getNombre() + " " + p.getAutor().getApellidos(),
                    p.getTexto(),
                    fotos,
                    p.getCreadaEn()
            );
        }).toList();
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Long id, Usuario usuario) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicación no encontrada con ID: " + id));

        validarOrganizadorOAdmin(publicacion.getEvento(), usuario);

        publicacionRepository.delete(publicacion);
    }

    private void notificarAsistentesNuevaPublicacion(Evento evento) {
        List<Inscripcion> inscritos = inscripcionRepository.findByEventoId(evento.getId());

        for (Inscripcion inscripcion : inscritos) {
            if (inscripcion.getEstado() == EstadoInscripcion.CON_PLAZA) {
                Notificacion noti = Notificacion.builder()
                        .usuario(inscripcion.getUsuario())
                        .tipo(TipoNotificacion.NUEVA_PUBLICACION)
                        .mensaje("Se ha añadido un resumen/fotos al evento: " + evento.getTitulo())
                        .recurso("EVENTO")
                        .recursoId(evento.getId())
                        .leida(false)
                        .build();
                notificacionRepository.save(noti);
            }
        }
    }

    private void validarOrganizadorOAdmin(Evento evento, Usuario usuario) {
        if (usuario.getRol() != Rol.ADMIN && !evento.getOrganizador().getId().equals(usuario.getId())) {
            throw new AccesoDenegadoException("No tienes permiso para publicar o gestionar resúmenes de este evento.");
        }
    }
}