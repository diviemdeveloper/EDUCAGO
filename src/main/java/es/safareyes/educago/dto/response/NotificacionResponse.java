package es.safareyes.educago.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        String tipo,
        String mensaje,
        String recurso,
        Long recursoId,
        Boolean leida,
        LocalDateTime creadaEn
) {}