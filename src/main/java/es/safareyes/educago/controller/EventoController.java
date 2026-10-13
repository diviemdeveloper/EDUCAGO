package es.safareyes.educago.controller;

import es.safareyes.educago.dto.request.CambiarEstadoEventoRequest;
import es.safareyes.educago.dto.request.CrearEventoRequest;
import es.safareyes.educago.dto.response.EventoCalendarioProjection;
import es.safareyes.educago.dto.response.EventoResponse;
import es.safareyes.educago.modelo.Usuario;
import es.safareyes.educago.repositorios.IUsuarioRepository;
import es.safareyes.educago.service.IEventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final IEventoService eventoService;
    private final IUsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<Page<EventoResponse>> buscarEventos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long cursoId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) LocalDateTime desde,
            @RequestParam(required = false) LocalDateTime hasta,
            Pageable pageable) {
        return ResponseEntity.ok(eventoService.buscarEventos(categoria, cursoId, texto, desde, hasta, pageable));
    }

    @GetMapping("/calendario")
    public ResponseEntity<List<EventoCalendarioProjection>> obtenerCalendario(
            @RequestParam int mes,
            @RequestParam int anio) {
        return ResponseEntity.ok(eventoService.obtenerCalendario(mes, anio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerDetalle(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<EventoResponse> crearEvento(
            @Valid @RequestBody CrearEventoRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crearEvento(request, usuario));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<EventoResponse> editarEvento(
            @PathVariable Long id,
            @Valid @RequestBody CrearEventoRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(eventoService.editarEvento(id, request, usuario));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ORGANIZADOR', 'ADMIN')")
    public ResponseEntity<EventoResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoEventoRequest request,
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        return ResponseEntity.ok(eventoService.cambiarEstado(id, request, usuario));
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow();
    }
}