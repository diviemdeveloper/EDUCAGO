package es.safareyes.educago.mapper;

import es.safareyes.educago.dto.response.InscripcionResponse;
import es.safareyes.educago.modelo.Inscripcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IInscripcionMapper {

    @Mapping(target = "eventoId", source = "evento.id")
    @Mapping(target = "tituloEvento", source = "evento.titulo")
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "nombreUsuario", source = "usuario.nombre")
    InscripcionResponse toResponse(Inscripcion inscripcion);
}