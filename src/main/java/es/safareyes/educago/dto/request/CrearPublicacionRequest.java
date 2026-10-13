package es.safareyes.educago.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPublicacionRequest(
        @NotBlank(message = "El texto de la publicación es obligatorio")
        @Size(min = 1, max = 2000, message = "El texto de la publicación debe tener entre 1 y 2000 caracteres")
        String texto,

        List<String> urlsFotos
) {}