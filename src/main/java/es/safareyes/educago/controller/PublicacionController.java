package es.safareyes.educago.controller;

import es.safareyes.educago.dto.request.CrearPublicacionRequest;
import es.safareyes.educago.dto.response.PublicacionResponse;
import es.safareyes.educago.modelo.Usuario;
import es.safareyes.educago.repositorios.IUsuarioRepository;
import es.safareyes.educago.service.IPublicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PublicacionController {

    private final IPublicacionService publicacionService;
    private final IUsuarioRepository usuarioRepository;

    // Endpoint 11: Crear publicación en un evento (ORGANIZADOR propio, ADMIN) (7.7)
    @PostMapping("/eventos/{eventoId}/publicaciones")
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<PublicacionResponse> crearPublicacion(
            @PathVariable Long eventoId,
            @Valid @RequestBody CrearPublicacionRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(publicacionService.crearPublicacion(eventoId, request, usuario));
    }

    // Endpoint 12: Listar publicaciones y fotos de un evento (Público) (7.7)
    @GetMapping("/eventos/{eventoId}/publicaciones")
    public ResponseEntity<List<PublicacionResponse>> obtenerPublicaciones(@PathVariable Long eventoId) {
        return ResponseEntity.ok(publicacionService.obtenerPublicacionesEvento(eventoId));
    }

    // Endpoint 13: Eliminar una publicación (ORGANIZADOR propio, ADMIN) (7.7)
    @DeleteMapping("/publicaciones/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<Void> eliminarPublicacion(
            @PathVariable Long id,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        publicacionService.eliminarPublicacion(id, usuario);
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}