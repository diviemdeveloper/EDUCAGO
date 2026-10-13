package es.safareyes.educago.service;

import es.safareyes.educago.dto.response.NotificacionResponse;
import es.safareyes.educago.modelo.Usuario;

import java.util.List;

public interface INotificacionService {

    // Consultar las notificaciones del usuario autenticado
    List<NotificacionResponse> obtenerMisNotificaciones(Usuario usuario);

    // Marcar una notificación como leída
    void marcarComoLeida(Long id, Usuario usuario);

    // RN-07: Tarea automatizada para enviar recordatorios 24 horas antes
    void enviarRecordatorios24Horas();
}