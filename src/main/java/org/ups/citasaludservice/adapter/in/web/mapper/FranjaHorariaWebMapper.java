package org.ups.citasaludservice.adapter.in.web.mapper;

import org.ups.citasaludservice.adapter.in.web.api.model.FranjaHorariaResponse;
import org.ups.citasaludservice.domain.model.FranjaHoraria;

public class FranjaHorariaWebMapper {

    public FranjaHorariaResponse toDto(FranjaHoraria franja) {
        FranjaHorariaResponse dto = new FranjaHorariaResponse();
        dto.setId(franja.id());
        dto.setHoraInicio(franja.horaInicio().toString());
        dto.setHoraFin(franja.horaFin().toString());
        dto.setEstado(FranjaHorariaResponse.EstadoEnum.valueOf(franja.estado().name()));
        return dto;
    }
}
