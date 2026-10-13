package es.safareyes.educago.dto.request;

import jakarta.validation.constraints.NotNull;

public record MarcarAsistenciaRequest(

        @NotNull(message = "El estado de asistencia es obligatorio")
        Boolean asistio
) {}