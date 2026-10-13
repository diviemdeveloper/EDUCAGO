package es.safareyes.educago.dto.response;

import java.time.LocalDateTime;

public record InscripcionResponse(
        Long id,
        Long eventoId,
        String tituloEvento,
        Long usuarioId,
        String nombreUsuario,
        String tipo,          // ME_INTERESA, ASISTIRE
        String estado,        // CON_PLAZA, EN_ESPERA
        Integer posicionEspera,
        Boolean asistio,
        LocalDateTime creadaEn
) {}