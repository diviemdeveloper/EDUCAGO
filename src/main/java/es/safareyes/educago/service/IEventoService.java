package es.safareyes.educago.service;

import es.safareyes.educago.dto.request.CambiarEstadoEventoRequest;
import es.safareyes.educago.dto.request.CrearEventoRequest;
import es.safareyes.educago.dto.response.EventoCalendarioProjection;
import es.safareyes.educago.dto.response.EventoResponse;
import es.safareyes.educago.modelo.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventoService {

    Page<EventoResponse> buscarEventos(String categoria, Long cursoId, String texto, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    List<EventoCalendarioProjection> obtenerCalendario(int mes, int anio);

    EventoResponse obtenerDetalle(Long id);

    EventoResponse crearEvento(CrearEventoRequest request, Usuario organizador);

    EventoResponse editarEvento(Long id, CrearEventoRequest request, Usuario autor);

    EventoResponse cambiarEstado(Long id, CambiarEstadoEventoRequest request, Usuario autor);
}