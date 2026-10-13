package es.safareyes.educago.dto.response;

public record UsuarioResponse(
        Long id,
        String email,
        String nombre,
        String apellidos,
        String rol,
        String nombreCurso
) {}