package es.safareyes.educago.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record EventoResponse(
        Long id,
        String titulo,
        String descripcion,
        String requisitos,
        LocalDateTime inicio,
        LocalDateTime fin,
        String estado,
        Integer aforoMaximo,
        LocalDateTime fechaLimiteInscripcion,
        String nombreLugar,
        String nombreCategoria,
        String iconoCategoria,
        String colorCategoria,
        Long organizadorId,
        String nombreOrganizador,
        Set<String> cursosPermitidos,
        long plazasLibres
) {}