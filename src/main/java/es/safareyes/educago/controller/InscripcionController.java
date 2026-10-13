package es.safareyes.educago.controller;

import es.safareyes.educago.dto.request.CrearInscripcionRequest;
import es.safareyes.educago.dto.request.MarcarAsistenciaRequest;
import es.safareyes.educago.dto.response.InscripcionResponse;
import es.safareyes.educago.modelo.Usuario;
import es.safareyes.educago.repositorios.IUsuarioRepository;
import es.safareyes.educago.service.IInscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos/{eventoId}/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final IInscripcionService inscripcionService;
    private final IUsuarioRepository usuarioRepository;

    // Endpoint 7: Inscribirse en un evento (ESTUDIANTE, ORGANIZADOR, ADMIN)
    @PostMapping
    public ResponseEntity<InscripcionResponse> inscribir(
            @PathVariable Long eventoId,
            @Valid @RequestBody CrearInscripcionRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscripcionService.inscribir(eventoId, request, usuario));
    }

    // Endpoint 8: Cancelar la propia inscripción
    @DeleteMapping
    public ResponseEntity<Void> cancelarInscripcion(
            @PathVariable Long eventoId,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        inscripcionService.cancelarInscripcion(eventoId, usuario);
        return ResponseEntity.noContent().build();
    }

    // Endpoint 9: Listado de inscritos y lista de espera (ORGANIZADOR propio, ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<List<InscripcionResponse>> obtenerInscripciones(
            @PathVariable Long eventoId,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(inscripcionService.obtenerInscripcionesEvento(eventoId, usuario));
    }

    // Endpoint 10: Marcar asistencia (ORGANIZADOR propio, ADMIN)
    @PatchMapping("/{inscripcionId}/asistencia")
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<InscripcionResponse> marcarAsistencia(
            @PathVariable Long eventoId,
            @PathVariable Long inscripcionId,
            @Valid @RequestBody MarcarAsistenciaRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(inscripcionService.marcarAsistencia(eventoId, inscripcionId, request, usuario));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
    }
}