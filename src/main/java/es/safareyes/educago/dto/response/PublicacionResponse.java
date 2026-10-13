package es.safareyes.educago.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PublicacionResponse(
        Long id,
        Long eventoId,
        Long autorId,
        String nombreAutor,
        String texto,
        List<String> urlsFotos,
        LocalDateTime creadaEn
) {}