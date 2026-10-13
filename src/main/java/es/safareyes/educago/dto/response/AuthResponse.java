package es.safareyes.educago.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        String email,
        String rol
) {}