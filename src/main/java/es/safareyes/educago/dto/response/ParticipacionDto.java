package es.safareyes.educago.dto.response;

public record ParticipacionDto(
        String categoria,
        String curso,
        Long totalAsistentes
) {}