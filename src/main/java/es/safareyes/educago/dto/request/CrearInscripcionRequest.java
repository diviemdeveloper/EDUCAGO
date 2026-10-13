package es.safareyes.educago.dto.request;

import es.safareyes.educago.modelo.TipoInscripcion;
import jakarta.validation.constraints.NotNull;

public record CrearInscripcionRequest(

        @NotNull(message = "El tipo de inscripción es obligatorio")
        TipoInscripcion tipo
) {}