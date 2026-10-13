package es.safareyes.educago.dto.request;

import es.safareyes.educago.modelo.EstadoEvento;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoEventoRequest(

        @NotNull(message = "El estado del evento es obligatorio")
        EstadoEvento estado
) {}