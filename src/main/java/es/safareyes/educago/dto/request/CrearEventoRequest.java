package es.safareyes.educago.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public record CrearEventoRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar los 200 caracteres")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        String requisitos,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Future(message = "La fecha de inicio debe ser una fecha futura")
        LocalDateTime inicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        @Future(message = "La fecha de fin debe ser una fecha futura")
        LocalDateTime fin,

        @NotNull(message = "El aforo máximo es obligatorio")
        @Min(value = 1, message = "El aforo máximo debe ser de al menos 1 persona")
        Integer aforoMaximo,

        @NotNull(message = "La fecha límite de inscripción es obligatoria")
        LocalDateTime fechaLimiteInscripcion,

        @NotNull(message = "El ID del lugar es obligatorio")
        Long lugarId,

        @NotNull(message = "El ID de la categoría es obligatorio")
        Long categoriaId,

        Set<Long> cursosIds // Si está vacío o es null, el evento es abierto para todo el centro (RN-03)
) {}