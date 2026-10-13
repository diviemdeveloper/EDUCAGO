package es.safareyes.educago.service;

import es.safareyes.educago.dto.request.CrearPublicacionRequest;
import es.safareyes.educago.dto.response.PublicacionResponse;
import es.safareyes.educago.modelo.Usuario;

import java.util.List;

public interface IPublicacionService {

    // Endpoint 11: Crear publicación con fotos tras finalizar evento
    PublicacionResponse crearPublicacion(Long eventoId, CrearPublicacionRequest request, Usuario autor);

    // Endpoint 12: Consultar publicaciones/fotos de un evento
    List<PublicacionResponse> obtenerPublicacionesEvento(Long eventoId);

    // Endpoint 13: Eliminar publicación
    void eliminarPublicacion(Long id, Usuario usuario);
}