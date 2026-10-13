package es.safareyes.educago.service;

import es.safareyes.educago.dto.request.CrearInscripcionRequest;
import es.safareyes.educago.dto.request.MarcarAsistenciaRequest;
import es.safareyes.educago.dto.response.InscripcionResponse;
import es.safareyes.educago.modelo.Usuario;

import java.util.List;

public interface IInscripcionService {

    // Endpoint 7: Inscribirse ("Me interesa" o "Asistiré")
    InscripcionResponse inscribir(Long eventoId, CrearInscripcionRequest request, Usuario usuario);

    // Endpoint 8: Cancelar inscripción (con reasignación automática de lista de espera)
    void cancelarInscripcion(Long eventoId, Usuario usuario);

    // Endpoint 9: Listar inscripciones de un evento (para el Organizador/Admin)
    List<InscripcionResponse> obtenerInscripcionesEvento(Long eventoId, Usuario usuario);

    // Endpoint 10: Marcar asistencia del alumno
    InscripcionResponse marcarAsistencia(Long eventoId, Long inscripcionId, MarcarAsistenciaRequest request, Usuario organizador);
}