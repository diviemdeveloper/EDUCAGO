package es.safareyes.educago.controller;

import es.safareyes.educago.dto.response.NotificacionResponse;
import es.safareyes.educago.modelo.Usuario;
import es.safareyes.educago.repositorios.IUsuarioRepository;
import es.safareyes.educago.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final INotificacionService notificacionService;
    private final IUsuarioRepository usuarioRepository;

    // Endpoint: Obtener notificaciones del usuario autenticado
    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> obtenerMisNotificaciones(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(notificacionService.obtenerMisNotificaciones(usuario));
    }

    // Endpoint: Marcar notificación como leída
    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        notificacionService.marcarComoLeida(id, usuario);
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}