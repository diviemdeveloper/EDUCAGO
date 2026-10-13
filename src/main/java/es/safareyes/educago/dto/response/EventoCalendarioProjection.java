package es.safareyes.educago.dto.response;

import java.time.LocalDateTime;

public interface EventoCalendarioProjection {
    Long getId();
    String getTitulo();
    LocalDateTime getInicio();
    LocalDateTime getFin();
    String getNombreCategoria();
    String getIconoCategoria();
    String getColorCategoria();
}