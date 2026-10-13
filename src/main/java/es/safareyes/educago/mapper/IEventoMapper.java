package es.safareyes.educago.mapper;

import es.safareyes.educago.dto.response.EventoResponse;
import es.safareyes.educago.modelo.Curso;
import es.safareyes.educago.modelo.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IEventoMapper {

    @Mapping(target = "nombreLugar", source = "lugar.nombre")
    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    @Mapping(target = "iconoCategoria", source = "categoria.icono")
    @Mapping(target = "colorCategoria", source = "categoria.color")
    @Mapping(target = "organizadorId", source = "organizador.id")
    @Mapping(target = "nombreOrganizador", source = "organizador.nombre")
    @Mapping(target = "cursosPermitidos", source = "cursos", qualifiedByName = "mapCursosANombres")
    @Mapping(target = "plazasLibres", ignore = true) // Se calcula en el servicio
    EventoResponse toResponse(Evento evento);

    @Named("mapCursosANombres")
    default Set<String> mapCursosANombres(Set<Curso> cursos) {
        if (cursos == null) return Set.of();
        return cursos.stream().map(Curso::getNombre).collect(Collectors.toSet());
    }
}